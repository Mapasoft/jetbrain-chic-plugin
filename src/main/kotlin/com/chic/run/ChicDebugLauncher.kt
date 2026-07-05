package com.chic.run

import com.intellij.execution.ProgramRunnerUtil
import com.intellij.execution.DefaultExecutionResult
import com.intellij.execution.ExecutionResult
import com.intellij.execution.RunManager
import com.intellij.execution.configurations.ConfigurationTypeUtil
import com.intellij.execution.configurations.GeneralCommandLine
import com.intellij.execution.executors.DefaultDebugExecutor
import com.intellij.execution.filters.TextConsoleBuilderFactory
import com.intellij.execution.process.ProcessAdapter
import com.intellij.execution.process.ProcessEvent
import com.intellij.execution.runners.ExecutionEnvironment
import com.intellij.execution.runners.RunContentBuilder
import com.intellij.execution.ui.ConsoleView
import com.intellij.execution.ui.RunContentDescriptor
import com.intellij.notification.NotificationGroupManager
import com.intellij.notification.NotificationType
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.project.Project
import com.intellij.util.execution.ParametersListUtil
import com.jetbrains.cidr.execution.BuildTargetAndConfigurationData
import com.jetbrains.cidr.cpp.execution.external.build.CLionExternalBuildConfiguration
import com.jetbrains.cidr.cpp.execution.external.build.CLionExternalBuildManager
import com.jetbrains.cidr.cpp.execution.external.build.CLionExternalBuildTarget
import com.jetbrains.cidr.cpp.execution.external.run.CLionExternalRunConfiguration
import com.jetbrains.cidr.cpp.execution.external.run.CLionExternalRunConfigurationType
import com.jetbrains.cidr.execution.ExecutableData
import java.util.UUID

object ChicDebugLauncher {

    fun launch(
        environment: ExecutionEnvironment,
        compilerOverridePath: String = "",
        chicArguments: String = "",
        programArguments: String = ""
    ): RunContentDescriptor? {
        val project = environment.project
        val compiler = ChicCompilerDetector.resolveCompiler(project, compilerOverridePath)
        if (compiler == null) {
            notify(project, "Chic compiler was not found. Set CHIC_DIR or use Override compiler path.", NotificationType.ERROR)
            return null
        }

        val paths = ChicBuildPaths.forProject(project)
        val arguments = mutableListOf("build", paths.projectDir.path, "-out", paths.executable.path)
        arguments.addAll(ParametersListUtil.parse(chicArguments))
        if ("-g" !in arguments) arguments.add("-g")

        val commandLine = GeneralCommandLine(compiler)
            .withParameters(arguments)
            .withWorkDirectory(paths.projectDir)
            .withRedirectErrorStream(true)

        val processHandler = ChicProcessHandler(commandLine)
        processHandler.setShouldDestroyProcessRecursively(true)
        processHandler.addProcessListener(object : ProcessAdapter() {
            override fun processTerminated(event: ProcessEvent) {
                if (event.exitCode == 0 && paths.executable.isFile) {
                    ApplicationManager.getApplication().invokeLater {
                        launchNativeDebugger(project, paths.executable.path, paths.projectDir.path, programArguments)
                    }
                }
            }
        })

        val console: ConsoleView = TextConsoleBuilderFactory.getInstance()
            .createBuilder(project)
            .apply { addFilter(ChicErrorFilter(project)) }
            .console
        console.attachToProcess(processHandler)

        val result: ExecutionResult = DefaultExecutionResult(console, processHandler)
        val descriptor = RunContentBuilder(result, environment).showRunContent(environment.contentToReuse)
        processHandler.startNotify()
        return descriptor
    }

    private fun launchNativeDebugger(
        project: Project,
        executablePath: String,
        workingDirectory: String,
        programArguments: String
    ) {
        val settings = createNativeDebugConfiguration(project, executablePath, workingDirectory, programArguments)
        ApplicationManager.getApplication().invokeLater {
            ProgramRunnerUtil.executeConfiguration(settings, DefaultDebugExecutor.getDebugExecutorInstance())
        }
    }

    private fun createNativeDebugConfiguration(
        project: Project,
        executablePath: String,
        workingDirectory: String,
        programArguments: String
    ) =
        RunManager.getInstance(project).let { runManager ->
            val type = ConfigurationTypeUtil.findConfigurationType(CLionExternalRunConfigurationType::class.java)
            val settings = runManager.createConfiguration("Debug Chic", type.configurationFactories.single())
            val configuration = settings.configuration as CLionExternalRunConfiguration
            configuration.executableData = ExecutableData(executablePath)
            configuration.programParameters = programArguments
            configuration.workingDirectory = workingDirectory
            val target = ensureChicExternalTarget(project)
            val buildConfiguration = target.buildConfigurations.first()
            configuration.setTargetAndConfigurationData(BuildTargetAndConfigurationData(target, buildConfiguration))
            settings
        }

    private fun ensureChicExternalTarget(project: Project): CLionExternalBuildTarget {
        val manager = CLionExternalBuildManager.getInstance(project)
        manager.targets.firstOrNull { it.name == CHIC_TARGET_NAME }?.let { return it }

        val buildConfiguration = CLionExternalBuildConfiguration(
            CHIC_TARGET_NAME,
            null,
            null,
            null,
            UUID.randomUUID()
        )
        val target = CLionExternalBuildTarget(
            CHIC_TARGET_NAME,
            project.name,
            listOf(buildConfiguration)
        )
        manager.targets = manager.targets + target
        return target
    }

    private fun notify(project: Project, content: String, type: NotificationType) {
        NotificationGroupManager.getInstance()
            .getNotificationGroup("Chic")
            .createNotification(content, type)
            .notify(project)
    }

    private const val CHIC_TARGET_NAME = "Chic"
}

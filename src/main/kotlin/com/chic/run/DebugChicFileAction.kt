package com.chic.run

import com.chic.ChicFileType
import com.chic.ChicIcons
import com.intellij.execution.ProgramRunnerUtil
import com.intellij.execution.RunManager
import com.intellij.execution.configurations.ConfigurationTypeUtil
import com.intellij.execution.configurations.GeneralCommandLine
import com.intellij.execution.executors.DefaultDebugExecutor
import com.intellij.execution.process.CapturingProcessHandler
import com.intellij.notification.NotificationGroupManager
import com.intellij.notification.NotificationType
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.progress.ProgressManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.jetbrains.cidr.cpp.execution.compound.CLionNativeAppRunConfiguration
import com.jetbrains.cidr.cpp.execution.compound.CLionNativeAppRunConfigurationType
import com.jetbrains.cidr.execution.ExecutableData

class DebugChicFileAction : AnAction("Debug Chic File", "Build with debug info and launch CLion's native debugger", ChicIcons.FILE) {

    override fun update(event: AnActionEvent) {
        val project = event.project
        val file = event.getData(CommonDataKeys.VIRTUAL_FILE)
        event.presentation.isEnabledAndVisible = project != null && file?.fileType == ChicFileType.INSTANCE
    }

    override fun actionPerformed(event: AnActionEvent) {
        val project = event.project ?: return
        val file = event.getData(CommonDataKeys.VIRTUAL_FILE) ?: return
        val compiler = ChicCompilerDetector.resolveCompiler(project)

        if (compiler == null) {
            notify(project, "Chic compiler was not found. Set it in Settings | Tools | Chic.", NotificationType.ERROR)
            return
        }

        val paths = ChicBuildPaths.forFile(project, file)
        ProgressManager.getInstance().runProcessWithProgressSynchronously(
            { buildAndDebug(project, compiler, paths) },
            "Building Chic Debug Binary",
            true,
            project
        )
    }

    private fun buildAndDebug(project: Project, compiler: String, paths: ChicBuildPaths) {
        val commandLine = GeneralCommandLine(compiler)
            .withParameters("build-exec", paths.projectDir.path, "-o", paths.outputDir.path, "-g")
            .withWorkDirectory(paths.projectDir)
            .withRedirectErrorStream(true)

        val output = CapturingProcessHandler(commandLine).runProcess()
        if (output.exitCode != 0 || !paths.executable.isFile) {
            notify(project, output.stdout.ifBlank { "Chic debug build failed." }, NotificationType.ERROR)
            return
        }

        val settings = createNativeDebugConfiguration(project, paths.executable.path)
        ApplicationManager.getApplication().invokeLater {
            ProgramRunnerUtil.executeConfiguration(settings, DefaultDebugExecutor.getDebugExecutorInstance())
        }
    }

    private fun createNativeDebugConfiguration(project: Project, executablePath: String) =
        RunManager.getInstance(project).let { runManager ->
            val type = ConfigurationTypeUtil.findConfigurationType(CLionNativeAppRunConfigurationType::class.java)
            val settings = runManager.createConfiguration("Debug Chic", type.configurationFactories.single())
            val configuration = settings.configuration as CLionNativeAppRunConfiguration
            configuration.executableData = ExecutableData(executablePath)
            runManager.setTemporaryConfiguration(settings)
            settings
        }

    private fun notify(project: Project, content: String, type: NotificationType) {
        NotificationGroupManager.getInstance()
            .getNotificationGroup("Chic")
            .createNotification(content, type)
            .notify(project)
    }
}

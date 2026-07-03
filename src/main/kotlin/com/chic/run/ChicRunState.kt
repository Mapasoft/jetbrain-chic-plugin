package com.chic.run

import com.intellij.execution.DefaultExecutionResult
import com.intellij.execution.ExecutionResult
import com.intellij.execution.Executor
import com.intellij.execution.configurations.CommandLineState
import com.intellij.execution.configurations.GeneralCommandLine
import com.intellij.execution.process.ProcessHandler
import com.intellij.execution.runners.ExecutionEnvironment
import com.intellij.execution.runners.ProgramRunner
import com.intellij.execution.ui.ConsoleView
import com.intellij.execution.filters.TextConsoleBuilderFactory
import com.intellij.util.execution.ParametersListUtil

/**
 * Executes the Chic compiler and wires its stdout/stderr to a CLion console
 * with the [ChicErrorFilter] applied so error lines become clickable.
 *
 * Invocation:  `<chicBinary> build <projectRoot> -out <projectRoot>/bin/<projectName>`
 *
 * The working directory and output root are the CLion project base directory.
 */
class ChicRunState(
    environment: ExecutionEnvironment,
    private val config: ChicRunConfiguration
) : CommandLineState(environment) {

    override fun startProcess(): ProcessHandler {
        val paths = ChicBuildPaths.forProject(environment.project)
        val binary = ChicCompilerDetector.resolveCompiler(environment.project, config.compilerOverridePath)
            ?: config.compilerOverridePath.ifBlank { "chic" }
        val programArguments = ParametersListUtil.parse(config.programArguments)
        val action = if (programArguments.isEmpty()) "build" else "build-exec"

        val cmd = GeneralCommandLine(binary)
            .withParameters(action, paths.projectDir.path, "-out", paths.executable.path)
            .withParameters(ParametersListUtil.parse(config.chicArguments))
            .withParameters(programArguments)
            .withWorkDirectory(paths.projectDir)
            .withRedirectErrorStream(true)   // merge stderr → stdout for the console

        return ChicProcessHandler(cmd).also { handler ->
            handler.setShouldDestroyProcessRecursively(true)
        }
    }

    override fun execute(executor: Executor, runner: ProgramRunner<*>): ExecutionResult {
        val processHandler = startProcess()

        // Build a console that understands Chic error lines
        val console: ConsoleView = TextConsoleBuilderFactory.getInstance()
            .createBuilder(environment.project)
            .apply { addFilter(ChicErrorFilter(environment.project)) }
            .console

        console.attachToProcess(processHandler)
        return DefaultExecutionResult(console, processHandler)
    }
}

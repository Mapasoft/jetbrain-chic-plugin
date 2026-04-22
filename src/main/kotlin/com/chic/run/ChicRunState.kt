package com.chic.run

import com.chic.settings.ChicSettings
import com.intellij.execution.DefaultExecutionResult
import com.intellij.execution.ExecutionResult
import com.intellij.execution.Executor
import com.intellij.execution.configurations.CommandLineState
import com.intellij.execution.configurations.GeneralCommandLine
import com.intellij.execution.process.OSProcessHandler
import com.intellij.execution.process.ProcessHandler
import com.intellij.execution.runners.ExecutionEnvironment
import com.intellij.execution.runners.ProgramRunner
import com.intellij.execution.ui.ConsoleView
import com.intellij.execution.filters.TextConsoleBuilderFactory

/**
 * Executes the Chic compiler and wires its stdout/stderr to a CLion console
 * with the [ChicErrorFilter] applied so error lines become clickable.
 *
 * Invocation:  `<chicBinary> <sourceFile>`
 *
 * The working directory is set to the project base directory so that
 * relative import paths inside the source file resolve correctly.
 */
class ChicRunState(
    environment: ExecutionEnvironment,
    private val config: ChicRunConfiguration
) : CommandLineState(environment) {

    override fun startProcess(): ProcessHandler {
        val binary = resolveBinary()

        val cmd = GeneralCommandLine(binary)
            .withParameters(config.sourceFile)
            .withWorkDirectory(environment.project.basePath ?: ".")
            .withRedirectErrorStream(true)   // merge stderr → stdout for the console

        return OSProcessHandler(cmd).also { handler ->
            handler.setShouldDestroyProcessRecursively(true)
        }
    }

    // Per-run override wins; otherwise use the project-wide setting; fall back to PATH.
    private fun resolveBinary(): String {
        val perRun = config.chicBinary.trim()
        if (perRun.isNotEmpty() && perRun != "chic") return perRun

        val settingsPath = ChicSettings.getInstance(environment.project).compilerPath.trim()
        return settingsPath.ifEmpty { "chic" }
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

package com.chic.run

import com.intellij.execution.configurations.RunProfile
import com.intellij.execution.configurations.RunProfileState
import com.intellij.execution.configurations.RunnerSettings
import com.intellij.execution.executors.DefaultDebugExecutor
import com.intellij.execution.runners.ExecutionEnvironment
import com.intellij.execution.runners.GenericProgramRunner
import com.intellij.execution.ui.RunContentDescriptor

class ChicDebugProgramRunner : GenericProgramRunner<RunnerSettings>() {

    override fun getRunnerId(): String = "ChicDebugProgramRunner"

    override fun canRun(executorId: String, profile: RunProfile): Boolean =
        executorId == DefaultDebugExecutor.EXECUTOR_ID && profile is ChicRunConfiguration

    override fun doExecute(state: RunProfileState, environment: ExecutionEnvironment): RunContentDescriptor? {
        val configuration = environment.runProfile as? ChicRunConfiguration ?: return null
        return ChicDebugLauncher.launch(
            environment,
            configuration.compilerOverridePath,
            configuration.chicArguments
        )
    }
}

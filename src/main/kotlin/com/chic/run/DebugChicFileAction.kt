package com.chic.run

import com.chic.ChicIcons
import com.intellij.execution.ProgramRunnerUtil
import com.intellij.execution.RunManager
import com.intellij.execution.executors.DefaultDebugExecutor
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.project.Project

class DebugChicFileAction : AnAction("Debug Chic Project", "Build the current Chic project with debug info and launch CLion's native debugger", ChicIcons.FILE) {

    override fun update(event: AnActionEvent) {
        event.presentation.isEnabledAndVisible = ChicProjectDetector.isChicContext(event)
    }

    override fun actionPerformed(event: AnActionEvent) {
        val project = event.project ?: return
        val settings = createTemporaryConfiguration(project)
        ProgramRunnerUtil.executeConfiguration(settings, DefaultDebugExecutor.getDebugExecutorInstance())
    }

    private fun createTemporaryConfiguration(project: Project) =
        RunManager.getInstance(project).let { runManager ->
            val type = ChicRunConfigurationType.getInstance()
            val settings = runManager.createConfiguration("Debug ${project.name}", type.configurationFactories.single())
            val configuration = settings.configuration as ChicRunConfiguration
            configuration.compilerOverridePath = ""
            runManager.setTemporaryConfiguration(settings)
            settings
        }
}

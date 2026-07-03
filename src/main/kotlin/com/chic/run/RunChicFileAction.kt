package com.chic.run

import com.chic.ChicIcons
import com.intellij.execution.ProgramRunnerUtil
import com.intellij.execution.RunManager
import com.intellij.execution.executors.DefaultRunExecutor
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.project.Project

class RunChicFileAction : AnAction("Build Chic Project", "Build the current Chic project", ChicIcons.FILE) {

    override fun update(event: AnActionEvent) {
        event.presentation.isEnabledAndVisible = ChicProjectDetector.isChicContext(event)
    }

    override fun actionPerformed(event: AnActionEvent) {
        val project = event.project ?: return

        val settings = createTemporaryConfiguration(project)
        ProgramRunnerUtil.executeConfiguration(settings, DefaultRunExecutor.getRunExecutorInstance())
    }

    private fun createTemporaryConfiguration(project: Project) =
        RunManager.getInstance(project).let { runManager ->
            val type = ChicRunConfigurationType.getInstance()
            val settings = runManager.createConfiguration("Build ${project.name}", type.configurationFactories.single())
            val configuration = settings.configuration as ChicRunConfiguration
            configuration.compilerOverridePath = ""
            runManager.setTemporaryConfiguration(settings)
            settings
        }
}

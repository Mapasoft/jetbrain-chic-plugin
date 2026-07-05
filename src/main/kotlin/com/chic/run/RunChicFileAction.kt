package com.chic.run

import com.chic.ChicIcons
import com.intellij.execution.ProgramRunnerUtil
import com.intellij.execution.executors.DefaultRunExecutor
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent

class RunChicFileAction : AnAction("Build Chic Project", "Build the current Chic project", ChicIcons.FILE) {

    override fun update(event: AnActionEvent) {
        event.presentation.isEnabledAndVisible = ChicProjectDetector.isChicContext(event)
    }

    override fun actionPerformed(event: AnActionEvent) {
        val project = event.project ?: return
        val settings = ChicRunConfigurations.findOrCreate(project)
        ProgramRunnerUtil.executeConfiguration(settings, DefaultRunExecutor.getRunExecutorInstance())
    }
}

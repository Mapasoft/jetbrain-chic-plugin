package com.chic.run

import com.chic.ChicIcons
import com.intellij.execution.ProgramRunnerUtil
import com.intellij.execution.executors.DefaultDebugExecutor
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent

class DebugChicFileAction : AnAction("Debug Chic Project", "Build the current Chic project with debug info and launch CLion's native debugger", ChicIcons.FILE) {

    override fun update(event: AnActionEvent) {
        event.presentation.isEnabledAndVisible = ChicProjectDetector.isChicContext(event)
    }

    override fun actionPerformed(event: AnActionEvent) {
        val project = event.project ?: return
        val settings = ChicRunConfigurations.findOrCreate(project)
        ProgramRunnerUtil.executeConfiguration(settings, DefaultDebugExecutor.getDebugExecutorInstance())
    }
}

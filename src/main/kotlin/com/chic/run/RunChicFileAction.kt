package com.chic.run

import com.chic.ChicFileType
import com.chic.ChicIcons
import com.intellij.execution.ProgramRunnerUtil
import com.intellij.execution.RunManager
import com.intellij.execution.executors.DefaultRunExecutor
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile

class RunChicFileAction : AnAction("Run Chic File", "Compile and run the selected Chic file", ChicIcons.FILE) {

    override fun update(event: AnActionEvent) {
        val project = event.project
        val file = event.getData(CommonDataKeys.VIRTUAL_FILE)
        event.presentation.isEnabledAndVisible = project != null && file?.fileType == ChicFileType.INSTANCE
    }

    override fun actionPerformed(event: AnActionEvent) {
        val project = event.project ?: return
        val file = event.getData(CommonDataKeys.VIRTUAL_FILE) ?: return

        val settings = createTemporaryConfiguration(project, file)
        ProgramRunnerUtil.executeConfiguration(settings, DefaultRunExecutor.getRunExecutorInstance())
    }

    private fun createTemporaryConfiguration(project: Project, file: VirtualFile) =
        RunManager.getInstance(project).let { runManager ->
            val type = ChicRunConfigurationType.getInstance()
            val settings = runManager.createConfiguration("Run ${file.name}", type.configurationFactories.single())
            val configuration = settings.configuration as ChicRunConfiguration
            configuration.sourceFile = file.path
            configuration.chicBinary = ChicCompilerDetector.resolveCompiler(project) ?: "chic"
            runManager.setTemporaryConfiguration(settings)
            settings
        }
}

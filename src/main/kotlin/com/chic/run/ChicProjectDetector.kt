package com.chic.run

import com.chic.ChicFileType
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.project.Project
import java.io.File

object ChicProjectDetector {

    fun isChicContext(event: AnActionEvent): Boolean {
        val project = event.project ?: return false
        val file = event.getData(CommonDataKeys.VIRTUAL_FILE)
        return file?.fileType == ChicFileType.INSTANCE || containsChicSource(project)
    }

    private fun containsChicSource(project: Project): Boolean {
        val root = project.basePath?.let { File(it) } ?: return false
        if (!root.isDirectory) return false

        return root.walkTopDown()
            .onEnter { it.name !in SKIPPED_DIRS }
            .any { it.isFile && it.extension == "chic" }
    }

    private val SKIPPED_DIRS = setOf(".git", ".idea", "bin", "obj", "cmake-build-debug", "cmake-build-release")
}

package com.chic.run

import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import java.io.File

data class ChicBuildPaths(
    val projectDir: File,
    val outputDir: File,
    val executable: File
) {
    companion object {
        fun forFile(project: Project, file: VirtualFile): ChicBuildPaths {
            val projectDir = File(project.basePath ?: file.parent.path)
            return forProjectDir(projectDir)
        }

        fun forProject(project: Project): ChicBuildPaths {
            val projectDir = File(project.basePath ?: ".")
            return forProjectDir(projectDir)
        }

        private fun forProjectDir(projectDir: File): ChicBuildPaths {
            val outputDir = projectDir
            val executable = File(outputDir, "bin/${projectDir.name}")
            return ChicBuildPaths(projectDir, outputDir, executable)
        }
    }
}

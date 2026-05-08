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
            val outputDir = projectDir
            val outputName = outputDir.name
            val executable = File(outputDir, "$outputName/bin/$outputName")
            return ChicBuildPaths(projectDir, outputDir, executable)
        }
    }
}

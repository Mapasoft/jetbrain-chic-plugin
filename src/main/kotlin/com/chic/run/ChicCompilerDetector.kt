package com.chic.run

import com.chic.settings.ChicSettings
import com.intellij.execution.configurations.PathEnvironmentVariableUtil
import com.intellij.openapi.project.Project
import java.io.File

object ChicCompilerDetector {

    fun resolveCompiler(project: Project, overridePath: String? = null): String? {
        val candidates = listOfNotNull(
            overridePath?.trim()?.takeIf { it.isNotEmpty() && it != DEFAULT_BINARY },
            ChicSettings.getInstance(project).compilerPath.trim().takeIf { it.isNotEmpty() },
            findOnPath()?.absolutePath,
            findInProject(project)
        )

        return candidates.firstOrNull { isUsableCompiler(it) } ?: DEFAULT_BINARY.takeIf { findOnPath() != null }
    }

    fun isUsableCompiler(pathOrName: String): Boolean {
        if (pathOrName == DEFAULT_BINARY) return findOnPath() != null

        val file = File(pathOrName)
        return file.isFile && file.canExecute()
    }

    private fun findOnPath(): File? =
        PathEnvironmentVariableUtil.findInPath(DEFAULT_BINARY)

    private fun findInProject(project: Project): String? {
        val basePath = project.basePath ?: return null
        val candidates = listOf(
            "$basePath/$DEFAULT_BINARY",
            "$basePath/bin/$DEFAULT_BINARY",
            "$basePath/build/$DEFAULT_BINARY",
            "$basePath/cmake-build-debug/$DEFAULT_BINARY",
            "$basePath/cmake-build-release/$DEFAULT_BINARY"
        )

        return candidates.firstOrNull { isUsableCompiler(it) }
    }

    private const val DEFAULT_BINARY = "chic"
}

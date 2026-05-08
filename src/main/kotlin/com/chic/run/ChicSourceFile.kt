package com.chic.run

import com.intellij.openapi.project.Project
import java.io.File

object ChicSourceFile {

    fun resolve(project: Project, path: String): File {
        val file = File(path)
        if (file.isAbsolute) return file

        val basePath = project.basePath ?: return file
        return File(basePath, path)
    }
}

package com.chic.project

import com.chic.ChicIcons
import com.chic.run.ChicRunConfigurations
import com.intellij.facet.ui.ValidationResult
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.module.Module
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VfsUtil
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.platform.DirectoryProjectGeneratorBase
import com.intellij.platform.GeneratorPeerImpl
import com.intellij.platform.ProjectGeneratorPeer
import javax.swing.Icon

class ChicDirectoryProjectGenerator : DirectoryProjectGeneratorBase<Any>() {

    override fun getName(): String = "Chic"

    override fun getDescription(): String = "Create a Chic project"

    override fun getLogo(): Icon = ChicIcons.FILE

    override fun createPeer(): ProjectGeneratorPeer<Any> = GeneratorPeerImpl()

    override fun validate(baseDirPath: String): ValidationResult = ValidationResult.OK

    override fun generateProject(project: Project, baseDir: VirtualFile, settings: Any, module: Module) {
        ApplicationManager.getApplication().runWriteAction {
            writeFile(
                baseDir,
                "main.chic",
                """
                main : func() -> i32 {
                    return 0
                }
                """.trimIndent() + "\n"
            )
            writeFile(baseDir, "packages.txt", "")
        }
        ChicRunConfigurations.findOrCreate(project)
    }

    private fun writeFile(baseDir: VirtualFile, name: String, content: String) {
        val file = baseDir.findChild(name) ?: baseDir.createChildData(this, name)
        VfsUtil.saveText(file, content)
    }
}

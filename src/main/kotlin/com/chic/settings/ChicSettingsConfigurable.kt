package com.chic.settings

import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory
import com.intellij.openapi.options.Configurable
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.TextFieldWithBrowseButton
import com.intellij.ui.dsl.builder.AlignX
import com.intellij.ui.dsl.builder.panel
import javax.swing.JComponent

class ChicSettingsConfigurable(private val project: Project) : Configurable {

    private val compilerPathField = TextFieldWithBrowseButton().apply {
        addBrowseFolderListener(
            "Select Chic Compiler",
            "Path to the chic executable",
            project,
            FileChooserDescriptorFactory.createSingleFileDescriptor()
        )
    }

    override fun getDisplayName(): String = "Chic"

    override fun createComponent(): JComponent = panel {
        row("Chic compiler path:") {
            cell(compilerPathField).align(AlignX.FILL)
                .comment("Full path to the <code>chic</code> executable used by run configurations")
        }
    }

    override fun isModified(): Boolean =
        compilerPathField.text.trim() != ChicSettings.getInstance(project).compilerPath

    override fun apply() {
        ChicSettings.getInstance(project).compilerPath = compilerPathField.text.trim()
    }

    override fun reset() {
        compilerPathField.text = ChicSettings.getInstance(project).compilerPath
    }
}

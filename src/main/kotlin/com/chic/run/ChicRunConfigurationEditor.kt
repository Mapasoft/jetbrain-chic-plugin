package com.chic.run

import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory
import com.intellij.openapi.options.SettingsEditor
import com.intellij.openapi.ui.TextFieldWithBrowseButton
import com.intellij.ui.components.JBTextField
import com.intellij.ui.dsl.builder.AlignX
import com.intellij.ui.dsl.builder.panel
import javax.swing.JComponent

/**
 * The "Edit Run Configuration" panel for Chic.
 *
 * Shows two fields:
 *   1. **Chic binary** — path or name on PATH of the `chic` executable.
 *   2. **Source file** — `.chic` file to compile; comes with a file-chooser
 *      button that filters for `*.chic` files.
 */
class ChicRunConfigurationEditor : SettingsEditor<ChicRunConfiguration>() {

    private val binaryField     = JBTextField()
    private val sourceFileField = TextFieldWithBrowseButton()

    init {
        sourceFileField.addBrowseFolderListener(
            "Select Chic Source File",
            "Choose the .chic file to compile",
            /* project = */ null,
            FileChooserDescriptorFactory.createSingleFileDescriptor("chic")
        )
    }

    override fun resetEditorFrom(s: ChicRunConfiguration) {
        binaryField.text     = s.chicBinary
        sourceFileField.text = s.sourceFile
    }

    override fun applyEditorTo(s: ChicRunConfiguration) {
        s.chicBinary  = binaryField.text.trim()
        s.sourceFile  = sourceFileField.text.trim()
    }

    override fun createEditor(): JComponent = panel {
        row("Chic binary:") {
            cell(binaryField).align(AlignX.FILL)
                .comment("Name on PATH or full path to the <code>chic</code> executable")
        }
        row("Source file:") {
            cell(sourceFileField).align(AlignX.FILL)
        }
    }
}

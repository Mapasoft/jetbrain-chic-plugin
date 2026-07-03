package com.chic.run

import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory
import com.intellij.openapi.options.SettingsEditor
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.TextFieldWithBrowseButton
import com.intellij.ui.DocumentAdapter
import com.intellij.ui.components.JBTextField
import com.intellij.ui.dsl.builder.AlignX
import com.intellij.ui.dsl.builder.panel
import com.intellij.util.execution.ParametersListUtil
import javax.swing.event.DocumentEvent
import javax.swing.JComponent

/**
 * The "Edit Run Configuration" panel for Chic project builds.
 * Project root and output root come from the CLion project. The compiler is
 * normally discovered from CHIC_DIR, with this optional override for edge cases.
 */
class ChicRunConfigurationEditor : SettingsEditor<ChicRunConfiguration>() {

    private val compilerOverrideField = TextFieldWithBrowseButton()
    private val resolvedCompilerField = JBTextField().apply { isEditable = false }
    private val chicArgumentsField = JBTextField()
    private val programArgumentsField = JBTextField()
    private val resolvedArgumentsField = JBTextField().apply { isEditable = false }
    private var project: Project? = null

    init {
        compilerOverrideField.addBrowseFolderListener(
            "Select Chic Compiler",
            "Optional path to the chic executable",
            /* project = */ null,
            FileChooserDescriptorFactory.createSingleFileDescriptor()
        )
        compilerOverrideField.textField.document.addDocumentListener(object : DocumentAdapter() {
            override fun textChanged(e: DocumentEvent) {
                updateResolvedCompilerPath()
                updateResolvedArguments()
            }
        })
        chicArgumentsField.document.addDocumentListener(object : DocumentAdapter() {
            override fun textChanged(e: DocumentEvent) {
                updateResolvedArguments()
            }
        })
        programArgumentsField.document.addDocumentListener(object : DocumentAdapter() {
            override fun textChanged(e: DocumentEvent) {
                updateResolvedArguments()
            }
        })
    }

    override fun resetEditorFrom(s: ChicRunConfiguration) {
        project = s.project
        compilerOverrideField.text = s.compilerOverridePath
        chicArgumentsField.text = s.chicArguments
        programArgumentsField.text = s.programArguments
        updateResolvedCompilerPath()
        updateResolvedArguments()
    }

    override fun applyEditorTo(s: ChicRunConfiguration) {
        s.compilerOverridePath = compilerOverrideField.text.trim()
        s.chicArguments = chicArgumentsField.text.trim()
        s.programArguments = programArgumentsField.text.trim()
    }

    override fun createEditor(): JComponent = panel {
        row("Override compiler path:") {
            cell(compilerOverrideField).align(AlignX.FILL)
                .comment("Optional. Leave empty to use <code>CHIC_DIR/bin/chic</code> or <code>chic</code> on PATH.")
        }
        row("Resolved compiler path:") {
            cell(resolvedCompilerField).align(AlignX.FILL)
        }
        row("Chic arguments:") {
            cell(chicArgumentsField).align(AlignX.FILL)
                .comment("Optional arguments appended after <code>build &lt;projectRoot&gt; -out &lt;projectRoot&gt;/bin/&lt;projectName&gt;</code>.")
        }
        row("Program args:") {
            cell(programArgumentsField).align(AlignX.FILL)
                .comment("Optional. When set, the plugin uses <code>build-exec</code> and passes these arguments to the compiled program.")
        }
        row("All compiler arguments:") {
            cell(resolvedArgumentsField).align(AlignX.FILL)
        }
    }

    private fun updateResolvedCompilerPath() {
        val currentProject = project
        resolvedCompilerField.text = if (currentProject == null) {
            ""
        } else {
            ChicCompilerDetector.resolveCompiler(currentProject, compilerOverrideField.text.trim())
                ?: "Chic compiler not found"
        }
    }

    private fun updateResolvedArguments() {
        val currentProject = project
        if (currentProject?.basePath.isNullOrBlank()) {
            resolvedArgumentsField.text = ""
            return
        }
        val paths = ChicBuildPaths.forProject(currentProject!!)

        val extraArguments = parseArgumentsForDisplay(chicArgumentsField.text.trim())
        val programArguments = parseArgumentsForDisplay(programArgumentsField.text.trim())
        val action = if (programArguments.isEmpty()) "build" else "build-exec"
        val arguments = listOf(action, paths.projectDir.path, "-out", paths.executable.path) +
            extraArguments +
            programArguments
        resolvedArgumentsField.text = arguments.joinToString(" ") { formatArgument(it) }
    }

    private fun parseArgumentsForDisplay(text: String): List<String> =
        try {
            ParametersListUtil.parse(text)
        } catch (_: RuntimeException) {
            listOf(text).filter { it.isNotEmpty() }
        }

    private fun formatArgument(argument: String): String {
        if (argument.isEmpty()) return "\"\""
        val needsQuotes = argument.any { it.isWhitespace() || it == '"' || it == '\\' }
        if (!needsQuotes) return argument
        return "\"" + argument
            .replace("\\", "\\\\")
            .replace("\"", "\\\"") + "\""
    }
}

package com.chic.run

import com.intellij.execution.Executor
import com.intellij.execution.configurations.ConfigurationFactory
import com.intellij.execution.configurations.RunConfiguration
import com.intellij.execution.configurations.RunConfigurationBase
import com.intellij.execution.configurations.RunConfigurationOptions
import com.intellij.execution.configurations.RunProfileState
import com.intellij.execution.configurations.RuntimeConfigurationError
import com.intellij.execution.runners.ExecutionEnvironment
import com.intellij.openapi.options.SettingsEditor
import com.intellij.openapi.project.Project
import org.jdom.Element

/**
 * Holds the user-editable settings for a Chic build run configuration:
 *
 *  - [chicBinary]  — path (or name on PATH) of the `chic` executable
 *  - [sourceFile]  — the `.chic` file to pass to the compiler
 */
class ChicRunConfiguration(
    project: Project,
    factory: ConfigurationFactory,
    name: String
) : RunConfigurationBase<RunConfigurationOptions>(project, factory, name) {

    /** Path or name of the chic compiler binary. */
    var chicBinary: String = "chic"

    /** Source file to compile. */
    var sourceFile: String = ""

    // ── Lifecycle ────────────────────────────────────────────────────────────

    override fun getConfigurationEditor(): SettingsEditor<out RunConfiguration> =
        ChicRunConfigurationEditor()

    override fun checkConfiguration() {
        if (sourceFile.isBlank()) {
            throw RuntimeConfigurationError("Source file is not specified.")
        }
        val file = ChicSourceFile.resolve(project, sourceFile)
        if (!file.isFile) {
            throw RuntimeConfigurationError("Chic source file does not exist.")
        }
        if (file.extension != "chic") {
            throw RuntimeConfigurationError("Source file must use the .chic extension.")
        }
        if (ChicCompilerDetector.resolveCompiler(project, chicBinary) == null) {
            throw RuntimeConfigurationError("Chic compiler was not found. Set it in Settings | Tools | Chic.")
        }
    }

    override fun getState(executor: Executor, environment: ExecutionEnvironment): RunProfileState =
        ChicRunState(environment, this)

    // ── Persistence ──────────────────────────────────────────────────────────

    override fun readExternal(element: Element) {
        super.readExternal(element)
        chicBinary = element.getAttributeValue("chicBinary") ?: "chic"
        sourceFile = element.getAttributeValue("sourceFile") ?: ""
    }

    override fun writeExternal(element: Element) {
        super.writeExternal(element)
        element.setAttribute("chicBinary", chicBinary)
        element.setAttribute("sourceFile", sourceFile)
    }
}

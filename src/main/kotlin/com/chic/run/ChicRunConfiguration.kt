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
import java.io.File

/**
 * Holds the user-editable settings for a Chic project build configuration.
 * The project root is the CLion project base path. The compiler is discovered
 * from CHIC_DIR, Settings | Tools | Chic, PATH, or this optional override.
 */
class ChicRunConfiguration(
    project: Project,
    factory: ConfigurationFactory,
    name: String
) : RunConfigurationBase<RunConfigurationOptions>(project, factory, name) {

    /** Optional path or name of the chic compiler binary. */
    var compilerOverridePath: String = ""

    /** Extra arguments appended to the Chic compiler invocation. */
    var chicArguments: String = ""

    /** Arguments passed to the compiled program when launching under the debugger. */
    var programArguments: String = ""

    // ── Lifecycle ────────────────────────────────────────────────────────────

    override fun getConfigurationEditor(): SettingsEditor<out RunConfiguration> =
        ChicRunConfigurationEditor()

    override fun checkConfiguration() {
        val projectRoot = project.basePath?.let { File(it) }
            ?: throw RuntimeConfigurationError("Project root is not available.")
        if (!projectRoot.isDirectory) {
            throw RuntimeConfigurationError("Project root does not exist.")
        }
        if (!containsChicSource(projectRoot)) {
            throw RuntimeConfigurationError("No .chic source files were found under the project root.")
        }
        if (ChicCompilerDetector.resolveCompiler(project, compilerOverridePath) == null) {
            throw RuntimeConfigurationError("Chic compiler was not found. Set CHIC_DIR or use Override compiler path.")
        }
    }

    override fun getState(executor: Executor, environment: ExecutionEnvironment): RunProfileState =
        ChicRunState(environment, this)

    // ── Persistence ──────────────────────────────────────────────────────────

    override fun readExternal(element: Element) {
        super.readExternal(element)
        compilerOverridePath = element.getAttributeValue("compilerOverridePath")
            ?: element.getAttributeValue("chicBinary")
            ?: ""
        chicArguments = element.getAttributeValue("chicArguments") ?: ""
        programArguments = element.getAttributeValue("programArguments") ?: ""
    }

    override fun writeExternal(element: Element) {
        super.writeExternal(element)
        if (compilerOverridePath.isNotBlank()) {
            element.setAttribute("compilerOverridePath", compilerOverridePath)
        }
        if (chicArguments.isNotBlank()) {
            element.setAttribute("chicArguments", chicArguments)
        }
        if (programArguments.isNotBlank()) {
            element.setAttribute("programArguments", programArguments)
        }
    }

    private fun containsChicSource(root: File): Boolean =
        root.walkTopDown()
            .onEnter { it.name != "bin" && it.name != "obj" && it.name != ".git" && !it.name.endsWith(".idea") }
            .any { it.isFile && it.extension == "chic" }
}

package com.chic.run

import com.chic.ChicIcons
import com.intellij.execution.configurations.ConfigurationTypeBase
import com.intellij.execution.configurations.ConfigurationFactory
import com.intellij.execution.configurations.RunConfiguration
import com.intellij.execution.configurations.SimpleConfigurationType
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.NotNullLazyValue

/**
 * Registers the "Chic" entry in the Run > Edit Configurations dialog.
 *
 * Extends [SimpleConfigurationType] which provides a single factory and
 * handles the "New" template automatically.
 */
class ChicRunConfigurationType : SimpleConfigurationType(
    "ChicRunConfiguration",
    "Chic",
    "Compile and run a Chic source file",
    NotNullLazyValue.createValue { ChicIcons.FILE }
) {
    override fun createTemplateConfiguration(project: Project): RunConfiguration =
        ChicRunConfiguration(project, this, "Chic")
}

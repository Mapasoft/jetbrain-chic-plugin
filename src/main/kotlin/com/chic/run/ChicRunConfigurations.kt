package com.chic.run

import com.intellij.execution.RunManager
import com.intellij.execution.RunnerAndConfigurationSettings
import com.intellij.openapi.project.Project

object ChicRunConfigurations {

    fun findOrCreate(project: Project): RunnerAndConfigurationSettings {
        val runManager = RunManager.getInstance(project)
        current(runManager)?.let { return it }
        existing(runManager)?.let { return it }

        val type = ChicRunConfigurationType.getInstance()
        val settings = runManager.createConfiguration(project.name, type.configurationFactories.single())
        runManager.addConfiguration(settings)
        runManager.selectedConfiguration = settings
        return settings
    }

    private fun current(runManager: RunManager): RunnerAndConfigurationSettings? =
        runManager.selectedConfiguration?.takeIf { it.configuration is ChicRunConfiguration }

    private fun existing(runManager: RunManager): RunnerAndConfigurationSettings? =
        runManager.allSettings.firstOrNull { it.configuration is ChicRunConfiguration }
}

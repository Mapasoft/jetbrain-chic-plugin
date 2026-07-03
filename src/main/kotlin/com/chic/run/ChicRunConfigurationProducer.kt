package com.chic.run

import com.chic.parser.ChicFile
import com.intellij.execution.actions.ConfigurationContext
import com.intellij.execution.actions.LazyRunConfigurationProducer
import com.intellij.openapi.util.Ref
import com.intellij.psi.PsiElement

class ChicRunConfigurationProducer : LazyRunConfigurationProducer<ChicRunConfiguration>() {

    override fun setupConfigurationFromContext(
        configuration: ChicRunConfiguration,
        context: ConfigurationContext,
        sourceElement: Ref<PsiElement>
    ): Boolean {
        val chicFile = context.psiLocation?.containingFile as? ChicFile ?: return false

        configuration.name = suggestedName(context.project.name)
        configuration.compilerOverridePath = ""
        sourceElement.set(chicFile)
        return true
    }

    override fun isConfigurationFromContext(
        configuration: ChicRunConfiguration,
        context: ConfigurationContext
    ): Boolean {
        context.psiLocation?.containingFile as? ChicFile ?: return false
        return configuration.name == suggestedName(context.project.name)
    }

    override fun getConfigurationFactory() =
        ChicRunConfigurationType.getInstance().configurationFactories.single()

    private fun suggestedName(projectName: String): String =
        "Build $projectName"
}

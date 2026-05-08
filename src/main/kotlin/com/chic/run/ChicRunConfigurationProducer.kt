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
        val virtualFile = chicFile.virtualFile ?: return false

        configuration.name = suggestedName(virtualFile.nameWithoutExtension)
        configuration.sourceFile = virtualFile.path
        configuration.chicBinary = ChicCompilerDetector.resolveCompiler(context.project) ?: "chic"
        sourceElement.set(chicFile)
        return true
    }

    override fun isConfigurationFromContext(
        configuration: ChicRunConfiguration,
        context: ConfigurationContext
    ): Boolean {
        val chicFile = context.psiLocation?.containingFile as? ChicFile ?: return false
        val virtualFile = chicFile.virtualFile ?: return false
        return configuration.sourceFile == virtualFile.path
    }

    override fun getConfigurationFactory() =
        ChicRunConfigurationType.getInstance().configurationFactories.single()

    private fun suggestedName(fileName: String): String =
        "Run $fileName.chic"
}

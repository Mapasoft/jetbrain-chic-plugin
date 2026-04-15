package com.chic

import com.intellij.openapi.fileTypes.LanguageFileType
import javax.swing.Icon

/** File type for *.chic source files. */
class ChicFileType private constructor() : LanguageFileType(ChicLanguage.INSTANCE) {

    override fun getName(): String = "Chic"
    override fun getDescription(): String = "Chic source file"
    override fun getDefaultExtension(): String = "chic"
    override fun getIcon(): Icon = ChicIcons.FILE

    companion object {
        @JvmField
        val INSTANCE: ChicFileType = ChicFileType()
    }
}

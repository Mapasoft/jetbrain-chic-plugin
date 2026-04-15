package com.chic.parser

import com.chic.ChicFileType
import com.chic.ChicLanguage
import com.intellij.extapi.psi.PsiFileBase
import com.intellij.openapi.fileTypes.FileType
import com.intellij.psi.FileViewProvider

/** The PSI file node for a Chic source file. */
class ChicFile(viewProvider: FileViewProvider) : PsiFileBase(viewProvider, ChicLanguage.INSTANCE) {
    override fun getFileType(): FileType = ChicFileType.INSTANCE
    override fun toString(): String = "ChicFile"
}

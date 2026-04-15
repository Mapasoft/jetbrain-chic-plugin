package com.chic.editor

import com.chic.lexer.ChicTokenTypes
import com.intellij.lang.BracePair
import com.intellij.lang.PairedBraceMatcher
import com.intellij.psi.PsiFile
import com.intellij.psi.tree.IElementType

/**
 * Enables automatic brace / bracket / parenthesis matching in the editor.
 *
 * Curly braces are marked as structural so that the IDE uses them for
 * determining code block boundaries (e.g. expand-selection, smart indent).
 */
class ChicBraceMatcher : PairedBraceMatcher {

    private val pairs = arrayOf(
        BracePair(ChicTokenTypes.LBRACE,   ChicTokenTypes.RBRACE,   true),
        BracePair(ChicTokenTypes.LPAREN,   ChicTokenTypes.RPAREN,   false),
        BracePair(ChicTokenTypes.LBRACKET, ChicTokenTypes.RBRACKET, false)
    )

    override fun getPairs(): Array<BracePair> = pairs

    override fun isPairedBracesAllowedBeforeType(
        lbraceType: IElementType,
        contextType: IElementType?
    ): Boolean = true

    override fun getCodeConstructStart(file: PsiFile, openingBraceOffset: Int): Int =
        openingBraceOffset
}

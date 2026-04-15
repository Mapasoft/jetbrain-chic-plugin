package com.chic.parser

import com.intellij.lang.ASTNode
import com.intellij.lang.PsiBuilder
import com.intellij.lang.PsiParser
import com.intellij.psi.tree.IElementType

/**
 * Stub parser: builds a completely flat PSI tree where every token is a direct
 * child of the file node.  This is sufficient for v1 (syntax highlighting,
 * brace matching, code folding).  A full Grammar-Kit BNF parser can replace
 * this in a later release without changing any other plugin code.
 */
class ChicParser : PsiParser {
    override fun parse(root: IElementType, builder: PsiBuilder): ASTNode {
        val marker = builder.mark()
        while (!builder.eof()) {
            builder.advanceLexer()
        }
        marker.done(root)
        return builder.treeBuilt
    }
}

package com.chic.editor

import com.chic.lexer.ChicTokenTypes
import com.intellij.lang.ASTNode
import com.intellij.lang.folding.FoldingBuilderEx
import com.intellij.lang.folding.FoldingDescriptor
import com.intellij.openapi.editor.Document
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement

/**
 * Code-folding support for Chic files.
 *
 * Works on the flat PSI tree produced by [com.chic.parser.ChicParser]:
 * - Folds `{ … }` blocks that span more than one line.
 * - Folds `/* … */` block comments that span more than one line.
 *
 * The builder matches `{` / `}` tokens using a stack so nested blocks are
 * handled correctly.
 */
class ChicFoldingBuilder : FoldingBuilderEx() {

    override fun buildFoldRegions(
        root: PsiElement,
        document: Document,
        quick: Boolean
    ): Array<FoldingDescriptor> {
        val descriptors = mutableListOf<FoldingDescriptor>()
        val braceStack   = ArrayDeque<ASTNode>()

        var child = root.node.firstChildNode
        while (child != null) {
            when (child.elementType) {
                ChicTokenTypes.LBRACE -> braceStack.addLast(child)

                ChicTokenTypes.RBRACE -> {
                    if (braceStack.isNotEmpty()) {
                        val open = braceStack.removeLast()
                        val range = TextRange(
                            open.startOffset,
                            child.startOffset + child.textLength
                        )
                        if (document.getLineNumber(range.startOffset) <
                            document.getLineNumber(range.endOffset - 1)
                        ) {
                            descriptors.add(FoldingDescriptor(open, range))
                        }
                    }
                }

                ChicTokenTypes.BLOCK_COMMENT -> {
                    // Block comments may span many tokens (one per rule match);
                    // accumulate sibling BLOCK_COMMENT tokens into one region.
                    val start = child.startOffset
                    var end   = child.startOffset + child.textLength
                    var next  = child.treeNext
                    while (next != null && next.elementType == ChicTokenTypes.BLOCK_COMMENT) {
                        end  = next.startOffset + next.textLength
                        child = next
                        next  = child.treeNext
                    }
                    if (document.getLineNumber(start) < document.getLineNumber(end - 1)) {
                        val range = TextRange(start, end)
                        descriptors.add(
                            FoldingDescriptor(root.node.firstChildNode ?: root.node, range)
                        )
                    }
                }
            }
            child = child.treeNext
        }

        return descriptors.toTypedArray()
    }

    override fun getPlaceholderText(node: ASTNode): String =
        when (node.elementType) {
            ChicTokenTypes.LBRACE       -> "{ ... }"
            ChicTokenTypes.BLOCK_COMMENT -> "/* ... */"
            else -> "..."
        }

    override fun isCollapsedByDefault(node: ASTNode): Boolean = false
}

package com.chic.parser

import com.chic.lexer.ChicLexerAdapter
import com.chic.lexer.ChicTokenTypes
import com.intellij.lang.ASTNode
import com.intellij.lang.ParserDefinition
import com.intellij.lang.PsiParser
import com.intellij.lexer.Lexer
import com.intellij.openapi.project.Project
import com.intellij.psi.FileViewProvider
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.TokenType
import com.intellij.psi.tree.IFileElementType
import com.intellij.psi.tree.TokenSet
import com.chic.ChicLanguage
import com.intellij.psi.impl.source.tree.LeafPsiElement

class ChicParserDefinition : ParserDefinition {

    companion object {
        @JvmField
        val FILE: IFileElementType = IFileElementType(ChicLanguage.INSTANCE)
    }

    override fun createLexer(project: Project?): Lexer = ChicLexerAdapter()

    override fun createParser(project: Project?): PsiParser = ChicParser()

    override fun getFileNodeType(): IFileElementType = FILE

    override fun getWhitespaceTokens(): TokenSet = TokenSet.create(TokenType.WHITE_SPACE)

    override fun getCommentTokens(): TokenSet = ChicTokenTypes.COMMENTS

    override fun getStringLiteralElements(): TokenSet = ChicTokenTypes.STRING_LITERALS

    override fun createFile(viewProvider: FileViewProvider): PsiFile = ChicFile(viewProvider)

    override fun createElement(node: ASTNode): PsiElement = LeafPsiElement(node.elementType, node.text)
}

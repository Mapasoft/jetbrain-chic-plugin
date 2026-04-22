package com.chic.highlighting

import com.chic.lexer.ChicLexerAdapter
import com.chic.lexer.ChicTokenTypes
import com.intellij.lexer.Lexer
import com.intellij.openapi.editor.DefaultLanguageHighlighterColors
import com.intellij.openapi.editor.HighlighterColors
import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.editor.colors.TextAttributesKey.createTextAttributesKey
import com.intellij.openapi.fileTypes.SyntaxHighlighterBase
import com.intellij.psi.TokenType
import com.intellij.psi.tree.IElementType

class ChicSyntaxHighlighter : SyntaxHighlighterBase() {

    companion object {
        // ── Attribute keys (name → default colour from the IDE theme) ──────────

        @JvmField
        val KEYWORD = createTextAttributesKey(
            "CHIC_KEYWORD", DefaultLanguageHighlighterColors.KEYWORD
        )

        @JvmField
        val BUILTIN_TYPE = createTextAttributesKey(
            "CHIC_BUILTIN_TYPE", DefaultLanguageHighlighterColors.KEYWORD
        )

        @JvmField
        val BOOLEAN_LITERAL = createTextAttributesKey(
            "CHIC_BOOLEAN_LITERAL", DefaultLanguageHighlighterColors.KEYWORD
        )

        @JvmField
        val NUMBER = createTextAttributesKey(
            "CHIC_NUMBER", DefaultLanguageHighlighterColors.NUMBER
        )

        @JvmField
        val STRING = createTextAttributesKey(
            "CHIC_STRING", DefaultLanguageHighlighterColors.STRING
        )

        @JvmField
        val LINE_COMMENT = createTextAttributesKey(
            "CHIC_LINE_COMMENT", DefaultLanguageHighlighterColors.LINE_COMMENT
        )

        @JvmField
        val BLOCK_COMMENT = createTextAttributesKey(
            "CHIC_BLOCK_COMMENT", DefaultLanguageHighlighterColors.BLOCK_COMMENT
        )

        @JvmField
        val IDENTIFIER = createTextAttributesKey(
            "CHIC_IDENTIFIER", DefaultLanguageHighlighterColors.IDENTIFIER
        )

        @JvmField
        val OPERATOR = createTextAttributesKey(
            "CHIC_OPERATOR", DefaultLanguageHighlighterColors.OPERATION_SIGN
        )

        @JvmField
        val BRACES = createTextAttributesKey(
            "CHIC_BRACES", DefaultLanguageHighlighterColors.BRACES
        )

        @JvmField
        val PARENTHESES = createTextAttributesKey(
            "CHIC_PARENTHESES", DefaultLanguageHighlighterColors.PARENTHESES
        )

        @JvmField
        val BRACKETS = createTextAttributesKey(
            "CHIC_BRACKETS", DefaultLanguageHighlighterColors.BRACKETS
        )

        @JvmField
        val COMMA = createTextAttributesKey(
            "CHIC_COMMA", DefaultLanguageHighlighterColors.COMMA
        )

        @JvmField
        val DOT = createTextAttributesKey(
            "CHIC_DOT", DefaultLanguageHighlighterColors.DOT
        )

        @JvmField
        val BAD_CHAR = createTextAttributesKey(
            "CHIC_BAD_CHARACTER", HighlighterColors.BAD_CHARACTER
        )

        private val KEYWORD_KEYS      = arrayOf(KEYWORD)
        private val BUILTIN_KEYS      = arrayOf(BUILTIN_TYPE)
        private val BOOLEAN_KEYS      = arrayOf(BOOLEAN_LITERAL)
        private val NUMBER_KEYS       = arrayOf(NUMBER)
        private val STRING_KEYS       = arrayOf(STRING)
        private val LINE_CMT_KEYS     = arrayOf(LINE_COMMENT)
        private val BLOCK_CMT_KEYS    = arrayOf(BLOCK_COMMENT)
        private val IDENTIFIER_KEYS   = arrayOf(IDENTIFIER)
        private val OPERATOR_KEYS     = arrayOf(OPERATOR)
        private val BRACES_KEYS       = arrayOf(BRACES)
        private val PARENS_KEYS       = arrayOf(PARENTHESES)
        private val BRACKETS_KEYS     = arrayOf(BRACKETS)
        private val COMMA_KEYS        = arrayOf(COMMA)
        private val DOT_KEYS          = arrayOf(DOT)
        private val BAD_CHAR_KEYS     = arrayOf(BAD_CHAR)
    }

    override fun getHighlightingLexer(): Lexer = ChicLexerAdapter()

    override fun getTokenHighlights(tokenType: IElementType): Array<TextAttributesKey> =
        when {
            // Booleans before keywords (both are styled like keywords but distinguished)
            tokenType in ChicTokenTypes.BOOLEANS                -> BOOLEAN_KEYS
            tokenType in ChicTokenTypes.KEYWORDS                -> KEYWORD_KEYS
            tokenType == ChicTokenTypes.BUILTIN_TYPE            -> BUILTIN_KEYS

            // Literals
            tokenType == ChicTokenTypes.INT_LITERAL   ||
            tokenType == ChicTokenTypes.FLOAT_LITERAL ||
            tokenType == ChicTokenTypes.HEX_LITERAL   ||
            tokenType == ChicTokenTypes.BIN_LITERAL   ||
            tokenType == ChicTokenTypes.OCT_LITERAL             -> NUMBER_KEYS

            tokenType == ChicTokenTypes.STRING_LITERAL  ||
            tokenType == ChicTokenTypes.CSTRING_LITERAL ||
            tokenType == ChicTokenTypes.CHAR_LITERAL            -> STRING_KEYS

            // Comments
            tokenType == ChicTokenTypes.LINE_COMMENT            -> LINE_CMT_KEYS
            tokenType == ChicTokenTypes.BLOCK_COMMENT           -> BLOCK_CMT_KEYS

            // Brackets
            tokenType == ChicTokenTypes.LBRACE ||
            tokenType == ChicTokenTypes.RBRACE                  -> BRACES_KEYS

            tokenType == ChicTokenTypes.LPAREN ||
            tokenType == ChicTokenTypes.RPAREN                  -> PARENS_KEYS

            tokenType == ChicTokenTypes.LBRACKET ||
            tokenType == ChicTokenTypes.RBRACKET                -> BRACKETS_KEYS

            // Punctuation
            tokenType == ChicTokenTypes.COMMA                   -> COMMA_KEYS
            tokenType == ChicTokenTypes.DOT                     -> DOT_KEYS

            // Identifiers intentionally fall through to emptyArray() so they
            // inherit the editor's default text color — matching C/C++ behavior
            // where only semantically-resolved symbols get colored.

            // Operators (everything else that is not whitespace or unknown)
            tokenType == ChicTokenTypes.PLUS      || tokenType == ChicTokenTypes.PLUS_PLUS   ||
            tokenType == ChicTokenTypes.PLUS_EQ   || tokenType == ChicTokenTypes.MINUS       ||
            tokenType == ChicTokenTypes.MINUS_MINUS || tokenType == ChicTokenTypes.MINUS_EQ  ||
            tokenType == ChicTokenTypes.STAR      || tokenType == ChicTokenTypes.STAR_EQ     ||
            tokenType == ChicTokenTypes.SLASH     || tokenType == ChicTokenTypes.SLASH_EQ    ||
            tokenType == ChicTokenTypes.PERCENT   || tokenType == ChicTokenTypes.PERCENT_EQ  ||
            tokenType == ChicTokenTypes.AMP       || tokenType == ChicTokenTypes.AMP_AMP     ||
            tokenType == ChicTokenTypes.PIPE      || tokenType == ChicTokenTypes.PIPE_PIPE   ||
            tokenType == ChicTokenTypes.CARET     || tokenType == ChicTokenTypes.TILDE       ||
            tokenType == ChicTokenTypes.BANG      || tokenType == ChicTokenTypes.XOR         ||
            tokenType == ChicTokenTypes.EQ        || tokenType == ChicTokenTypes.ASSIGN      ||
            tokenType == ChicTokenTypes.DOUBLE_EQ || tokenType == ChicTokenTypes.NOT_EQ      ||
            tokenType == ChicTokenTypes.LT        || tokenType == ChicTokenTypes.LT_EQ       ||
            tokenType == ChicTokenTypes.GT        || tokenType == ChicTokenTypes.GT_EQ       ||
            tokenType == ChicTokenTypes.LSHIFT    || tokenType == ChicTokenTypes.LSHIFT_EQ   ||
            tokenType == ChicTokenTypes.RSHIFT    || tokenType == ChicTokenTypes.RSHIFT_EQ   ||
            tokenType == ChicTokenTypes.ARROW     || tokenType == ChicTokenTypes.FAT_ARROW   ||
            tokenType == ChicTokenTypes.COLON     || tokenType == ChicTokenTypes.DOUBLE_COLON ||
            tokenType == ChicTokenTypes.DOUBLE_DOT || tokenType == ChicTokenTypes.ELLIPSIS   ||
            tokenType == ChicTokenTypes.AT        || tokenType == ChicTokenTypes.SEMICOLON   -> OPERATOR_KEYS

            tokenType == TokenType.BAD_CHARACTER                -> BAD_CHAR_KEYS

            else -> emptyArray()
        }
}

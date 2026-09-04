package com.chic.lexer

import com.intellij.psi.tree.IElementType
import org.junit.Assert.assertEquals
import org.junit.Test

class ChicHighlightingLexerTest {

    @Test
    fun `enum declarations and variants use distinct token types`() {
        val source = """
            FilterMode : enum { Nearest, Linear }
            PayloadMode : enum { Data(i32, OtherType), Empty }
        """.trimIndent()

        val highlightedIdentifiers = identifierTokens(source)

        assertEquals(
            listOf(
                "FilterMode" to ChicTokenTypes.TYPE_DECLARATION_IDENTIFIER,
                "Nearest" to ChicTokenTypes.ENUM_VARIANT_IDENTIFIER,
                "Linear" to ChicTokenTypes.ENUM_VARIANT_IDENTIFIER,
                "PayloadMode" to ChicTokenTypes.TYPE_DECLARATION_IDENTIFIER,
                "Data" to ChicTokenTypes.ENUM_VARIANT_IDENTIFIER,
                "OtherType" to ChicTokenTypes.TYPE_REFERENCE_IDENTIFIER,
                "Empty" to ChicTokenTypes.ENUM_VARIANT_IDENTIFIER,
            ),
            highlightedIdentifiers,
        )
    }

    @Test
    fun `enum context is restored when highlighting restarts inside a file`() {
        val source = "FilterMode : enum { Nearest, Linear }"
        val restartOffset = source.indexOf("Linear")
        val lexer = ChicHighlightingLexer()

        lexer.start(source, restartOffset, source.length, 0)

        assertEquals(ChicTokenTypes.ENUM_VARIANT_IDENTIFIER, lexer.tokenType)
    }

    private fun identifierTokens(source: String): List<Pair<String, IElementType>> {
        val lexer = ChicHighlightingLexer()
        val result = mutableListOf<Pair<String, IElementType>>()
        lexer.start(source)

        while (lexer.tokenType != null) {
            val tokenType = lexer.tokenType!!
            if (tokenType == ChicTokenTypes.TYPE_DECLARATION_IDENTIFIER ||
                tokenType == ChicTokenTypes.TYPE_REFERENCE_IDENTIFIER ||
                tokenType == ChicTokenTypes.ENUM_VARIANT_IDENTIFIER
            ) {
                result += source.substring(lexer.tokenStart, lexer.tokenEnd) to tokenType
            }
            lexer.advance()
        }
        return result
    }
}

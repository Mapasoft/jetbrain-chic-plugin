package com.chic.lexer

import com.intellij.lexer.Lexer
import com.intellij.lexer.LexerBase
import com.intellij.psi.tree.IElementType

class ChicHighlightingLexer : LexerBase() {

    private val delegate: Lexer = ChicLexerAdapter()
    private var tokenType: IElementType? = null
    private var buffer: CharSequence = ""
    private var bufferEnd: Int = 0
    private val delimiterStack = mutableListOf<Delimiter>()
    private var previousMeaningfulToken: IElementType? = null

    private data class Delimiter(
        val openingToken: IElementType,
        val isEnumBody: Boolean = false,
    )

    override fun start(buffer: CharSequence, startOffset: Int, endOffset: Int, initialState: Int) {
        this.buffer = buffer
        this.bufferEnd = endOffset
        rebuildHighlightingContext(buffer, startOffset, endOffset)
        delegate.start(buffer, startOffset, endOffset, initialState)
        tokenType = refineTokenType()
    }

    override fun getState(): Int = delegate.state

    override fun getTokenType(): IElementType? = tokenType

    override fun getTokenStart(): Int = delegate.tokenStart

    override fun getTokenEnd(): Int = delegate.tokenEnd

    override fun advance() {
        updateHighlightingContext(delegate.tokenType)
        delegate.advance()
        tokenType = refineTokenType()
    }

    override fun getBufferSequence(): CharSequence = buffer

    override fun getBufferEnd(): Int = bufferEnd

    private fun refineTokenType(): IElementType? {
        val original = delegate.tokenType ?: return null
        if (original != ChicTokenTypes.IDENTIFIER) return original

        val text = buffer.subSequence(delegate.tokenStart, delegate.tokenEnd).toString()
        return when {
            text.startsWith("@") || previousMeaningfulChar() == '@' -> ChicTokenTypes.DECORATOR_IDENTIFIER
            isTextBasedDeclaration("func") -> ChicTokenTypes.FUNCTION_DECLARATION_IDENTIFIER
            isTextBasedDeclaration("struct", "enum", "union", "raw_union", "alias", "extension") ->
                ChicTokenTypes.TYPE_DECLARATION_IDENTIFIER
            isInsideExtensionTarget(text) -> ChicTokenTypes.TYPE_REFERENCE_IDENTIFIER
            isEnumVariantDeclaration() -> ChicTokenTypes.ENUM_VARIANT_IDENTIFIER
            isPascalCase(text) && previousMeaningfulChar() == '.' -> ChicTokenTypes.ENUM_VARIANT_IDENTIFIER
            isUpperSnakeCase(text) -> ChicTokenTypes.CONSTANT_IDENTIFIER
            isPascalCase(text) -> ChicTokenTypes.TYPE_REFERENCE_IDENTIFIER
            else -> original
        }
    }

    private fun rebuildHighlightingContext(buffer: CharSequence, startOffset: Int, endOffset: Int) {
        delimiterStack.clear()
        previousMeaningfulToken = null
        if (startOffset == 0) return

        val contextLexer = ChicLexerAdapter()
        contextLexer.start(buffer, 0, endOffset, 0)
        while (contextLexer.tokenType != null && contextLexer.tokenEnd <= startOffset) {
            updateHighlightingContext(contextLexer.tokenType)
            contextLexer.advance()
        }
    }

    private fun updateHighlightingContext(current: IElementType?) {
        when (current) {
            ChicTokenTypes.LBRACE -> delimiterStack += Delimiter(
                openingToken = ChicTokenTypes.LBRACE,
                isEnumBody = previousMeaningfulToken == ChicTokenTypes.KW_ENUM,
            )
            ChicTokenTypes.LPAREN -> delimiterStack += Delimiter(ChicTokenTypes.LPAREN)
            ChicTokenTypes.LBRACKET -> delimiterStack += Delimiter(ChicTokenTypes.LBRACKET)
            ChicTokenTypes.RBRACE -> popDelimiter(ChicTokenTypes.LBRACE)
            ChicTokenTypes.RPAREN -> popDelimiter(ChicTokenTypes.LPAREN)
            ChicTokenTypes.RBRACKET -> popDelimiter(ChicTokenTypes.LBRACKET)
        }

        if (current != null &&
            current != ChicTokenTypes.WHITE_SPACE &&
            current !in ChicTokenTypes.COMMENTS
        ) {
            previousMeaningfulToken = current
        }
    }

    private fun popDelimiter(openingToken: IElementType) {
        val matchingIndex = delimiterStack.indexOfLast { it.openingToken == openingToken }
        if (matchingIndex >= 0) {
            delimiterStack.subList(matchingIndex, delimiterStack.size).clear()
        }
    }

    private fun isEnumVariantDeclaration(): Boolean {
        if (delimiterStack.lastOrNull()?.isEnumBody != true) return false
        return previousMeaningfulToken == ChicTokenTypes.LBRACE ||
            previousMeaningfulToken == ChicTokenTypes.COMMA
    }

    private fun isTextBasedDeclaration(vararg kinds: String): Boolean {
        val after = buffer.subSequence(delegate.tokenEnd, bufferEnd).toString()
        val match = Regex("""^\s*:\s*([A-Za-z_][A-Za-z0-9_]*)""").find(after) ?: return false
        return match.groupValues[1] in kinds
    }

    private fun isInsideExtensionTarget(text: String): Boolean {
        if (!isPascalCase(text)) return false

        val before = buffer.subSequence(0, delegate.tokenStart).takeLast(32).toString()
        val after = buffer.subSequence(delegate.tokenEnd, bufferEnd).take(32).toString()

        return Regex("""extension\s*<\s*$""").containsMatchIn(before) &&
            Regex("""^\s*>""").containsMatchIn(after)
    }

    private fun previousMeaningfulChar(): Char? {
        var index = delegate.tokenStart - 1
        while (index >= 0) {
            val char = buffer[index]
            if (!char.isWhitespace()) return char
            index--
        }
        return null
    }

    private fun isPascalCase(s: String): Boolean {
        if (s.isEmpty() || !s[0].isUpperCase()) return false
        return s.any { it.isLowerCase() }
    }

    private fun isUpperSnakeCase(s: String): Boolean {
        if (s.length < 2) return false
        if (!s.all { it.isUpperCase() || it.isDigit() || it == '_' }) return false
        return s.any { it.isLetter() }
    }
}

package com.chic.lexer

import com.chic.ChicLanguage
import com.intellij.psi.TokenType
import com.intellij.psi.tree.IElementType
import com.intellij.psi.tree.TokenSet

/** A token element type that belongs to the Chic language. */
class ChicTokenType(debugName: String) : IElementType(debugName, ChicLanguage.INSTANCE) {
    override fun toString(): String = "ChicTokenType.${super.toString()}"
}

/**
 * All token element types produced by the Chic JFlex lexer.
 * The constants here must match the return values in ChicLexer.flex.
 */
object ChicTokenTypes {

    // ── Whitespace / bad character (re-export platform constants) ──────────

    @JvmField val WHITE_SPACE = TokenType.WHITE_SPACE
    @JvmField val BAD_CHARACTER = TokenType.BAD_CHARACTER

    // ── Comments ───────────────────────────────────────────────────────────

    @JvmField val LINE_COMMENT   = ChicTokenType("LINE_COMMENT")
    @JvmField val BLOCK_COMMENT  = ChicTokenType("BLOCK_COMMENT")

    // ── Literals ───────────────────────────────────────────────────────────

    @JvmField val INT_LITERAL     = ChicTokenType("INT_LITERAL")
    @JvmField val FLOAT_LITERAL   = ChicTokenType("FLOAT_LITERAL")
    @JvmField val HEX_LITERAL     = ChicTokenType("HEX_LITERAL")
    @JvmField val BIN_LITERAL     = ChicTokenType("BIN_LITERAL")
    @JvmField val OCT_LITERAL     = ChicTokenType("OCT_LITERAL")
    @JvmField val STRING_LITERAL  = ChicTokenType("STRING_LITERAL")
    @JvmField val CSTRING_LITERAL = ChicTokenType("CSTRING_LITERAL")
    @JvmField val CHAR_LITERAL    = ChicTokenType("CHAR_LITERAL")

    // ── Identifier / built-in types ────────────────────────────────────────

    @JvmField val IDENTIFIER   = ChicTokenType("IDENTIFIER")
    @JvmField val BUILTIN_TYPE = ChicTokenType("BUILTIN_TYPE")

    // Highlighter-only identifier refinements. The parser lexer still emits
    // IDENTIFIER; ChicHighlightingLexer maps selected identifiers to these
    // token types so non-project files get useful colors without daemon passes.
    @JvmField val TYPE_DECLARATION_IDENTIFIER = ChicTokenType("TYPE_DECLARATION_IDENTIFIER")
    @JvmField val FUNCTION_DECLARATION_IDENTIFIER = ChicTokenType("FUNCTION_DECLARATION_IDENTIFIER")
    @JvmField val TYPE_REFERENCE_IDENTIFIER = ChicTokenType("TYPE_REFERENCE_IDENTIFIER")
    @JvmField val ENUM_VARIANT_IDENTIFIER = ChicTokenType("ENUM_VARIANT_IDENTIFIER")
    @JvmField val CONSTANT_IDENTIFIER = ChicTokenType("CONSTANT_IDENTIFIER")
    @JvmField val DECORATOR_IDENTIFIER = ChicTokenType("DECORATOR_IDENTIFIER")

    // ── Brackets ───────────────────────────────────────────────────────────

    @JvmField val LBRACE   = ChicTokenType("LBRACE")
    @JvmField val RBRACE   = ChicTokenType("RBRACE")
    @JvmField val LPAREN   = ChicTokenType("LPAREN")
    @JvmField val RPAREN   = ChicTokenType("RPAREN")
    @JvmField val LBRACKET = ChicTokenType("LBRACKET")
    @JvmField val RBRACKET = ChicTokenType("RBRACKET")

    // ── Punctuation ────────────────────────────────────────────────────────

    @JvmField val COLON       = ChicTokenType("COLON")
    @JvmField val DOUBLE_COLON = ChicTokenType("DOUBLE_COLON")
    @JvmField val SEMICOLON   = ChicTokenType("SEMICOLON")
    @JvmField val COMMA       = ChicTokenType("COMMA")
    @JvmField val DOT         = ChicTokenType("DOT")
    @JvmField val DOUBLE_DOT  = ChicTokenType("DOUBLE_DOT")
    @JvmField val ELLIPSIS    = ChicTokenType("ELLIPSIS")
    @JvmField val AT          = ChicTokenType("AT")

    // ── Operators ──────────────────────────────────────────────────────────

    @JvmField val ARROW     = ChicTokenType("ARROW")
    @JvmField val FAT_ARROW = ChicTokenType("FAT_ARROW")
    @JvmField val CARET     = ChicTokenType("CARET")

    @JvmField val EQ        = ChicTokenType("EQ")
    @JvmField val ASSIGN    = ChicTokenType("ASSIGN")
    @JvmField val DOUBLE_EQ = ChicTokenType("DOUBLE_EQ")
    @JvmField val NOT_EQ    = ChicTokenType("NOT_EQ")

    @JvmField val LT        = ChicTokenType("LT")
    @JvmField val LT_EQ     = ChicTokenType("LT_EQ")
    @JvmField val GT        = ChicTokenType("GT")
    @JvmField val GT_EQ     = ChicTokenType("GT_EQ")
    @JvmField val LSHIFT    = ChicTokenType("LSHIFT")
    @JvmField val RSHIFT    = ChicTokenType("RSHIFT")
    @JvmField val LSHIFT_EQ = ChicTokenType("LSHIFT_EQ")
    @JvmField val RSHIFT_EQ = ChicTokenType("RSHIFT_EQ")

    @JvmField val PLUS      = ChicTokenType("PLUS")
    @JvmField val PLUS_PLUS = ChicTokenType("PLUS_PLUS")
    @JvmField val PLUS_EQ   = ChicTokenType("PLUS_EQ")
    @JvmField val MINUS     = ChicTokenType("MINUS")
    @JvmField val MINUS_MINUS = ChicTokenType("MINUS_MINUS")
    @JvmField val MINUS_EQ  = ChicTokenType("MINUS_EQ")
    @JvmField val STAR      = ChicTokenType("STAR")
    @JvmField val STAR_EQ   = ChicTokenType("STAR_EQ")
    @JvmField val SLASH     = ChicTokenType("SLASH")
    @JvmField val SLASH_EQ  = ChicTokenType("SLASH_EQ")
    @JvmField val PERCENT   = ChicTokenType("PERCENT")
    @JvmField val PERCENT_EQ = ChicTokenType("PERCENT_EQ")

    @JvmField val AMP      = ChicTokenType("AMP")
    @JvmField val AMP_AMP  = ChicTokenType("AMP_AMP")
    @JvmField val PIPE     = ChicTokenType("PIPE")
    @JvmField val PIPE_PIPE = ChicTokenType("PIPE_PIPE")
    @JvmField val TILDE    = ChicTokenType("TILDE")
    @JvmField val BANG     = ChicTokenType("BANG")
    @JvmField val XOR      = ChicTokenType("XOR")

    // ── Keywords ───────────────────────────────────────────────────────────

    @JvmField val KW_IMPORT    = ChicTokenType("KW_IMPORT")
    @JvmField val KW_NAMESPACE = ChicTokenType("KW_NAMESPACE")
    @JvmField val KW_LET       = ChicTokenType("KW_LET")
    @JvmField val KW_VAR       = ChicTokenType("KW_VAR")
    @JvmField val KW_FUNC      = ChicTokenType("KW_FUNC")
    @JvmField val KW_FOR       = ChicTokenType("KW_FOR")
    @JvmField val KW_IN        = ChicTokenType("KW_IN")
    @JvmField val KW_STEP      = ChicTokenType("KW_STEP")
    @JvmField val KW_CONTINUE  = ChicTokenType("KW_CONTINUE")
    @JvmField val KW_BREAK     = ChicTokenType("KW_BREAK")
    @JvmField val KW_IF        = ChicTokenType("KW_IF")
    @JvmField val KW_ELSE      = ChicTokenType("KW_ELSE")
    @JvmField val KW_RETURN    = ChicTokenType("KW_RETURN")
    @JvmField val KW_TRUE      = ChicTokenType("KW_TRUE")
    @JvmField val KW_FALSE     = ChicTokenType("KW_FALSE")
    @JvmField val KW_NULL      = ChicTokenType("KW_NULL")
    @JvmField val KW_NEW       = ChicTokenType("KW_NEW")
    @JvmField val KW_RELEASE   = ChicTokenType("KW_RELEASE")
    @JvmField val KW_SIZEOF    = ChicTokenType("KW_SIZEOF")
    @JvmField val KW_ENUM      = ChicTokenType("KW_ENUM")
    @JvmField val KW_STRUCT    = ChicTokenType("KW_STRUCT")
    @JvmField val KW_UNION     = ChicTokenType("KW_UNION")
    @JvmField val KW_RAW_UNION = ChicTokenType("KW_RAW_UNION")
    @JvmField val KW_ALIAS     = ChicTokenType("KW_ALIAS")
    @JvmField val KW_EXTENSION = ChicTokenType("KW_EXTENSION")
    @JvmField val KW_MATCH     = ChicTokenType("KW_MATCH")
    @JvmField val KW_CAST      = ChicTokenType("KW_CAST")
    @JvmField val KW_DEFER     = ChicTokenType("KW_DEFER")
    @JvmField val KW_INLINE    = ChicTokenType("KW_INLINE")
    @JvmField val KW_TRY       = ChicTokenType("KW_TRY")
    @JvmField val KW_OR_ELSE   = ChicTokenType("KW_OR_ELSE")
    @JvmField val KW_OR_ERROR  = ChicTokenType("KW_OR_ERROR")
    @JvmField val KW_CT_IF     = ChicTokenType("KW_CT_IF")
    @JvmField val KW_CT_ELSE   = ChicTokenType("KW_CT_ELSE")
    @JvmField val KW_CT_ELIF   = ChicTokenType("KW_CT_ELIF")
    @JvmField val KW_CT_END    = ChicTokenType("KW_CT_END")

    // ── Token sets for use in highlighter / parser ─────────────────────────

    /** All control-flow and declaration keywords. */
    @JvmField val KEYWORDS: TokenSet = TokenSet.create(
        KW_IMPORT, KW_NAMESPACE, KW_LET, KW_VAR, KW_FUNC, KW_FOR, KW_IN, KW_STEP,
        KW_CONTINUE, KW_BREAK, KW_IF, KW_ELSE, KW_RETURN, KW_NEW,
        KW_RELEASE, KW_SIZEOF, KW_ENUM, KW_STRUCT, KW_UNION, KW_RAW_UNION,
        KW_ALIAS, KW_EXTENSION, KW_MATCH, KW_CAST, KW_DEFER, KW_INLINE,
        KW_TRY, KW_OR_ELSE, KW_OR_ERROR
    )

    /** Boolean literals — highlighted separately from other keywords. */
    @JvmField val BOOLEANS: TokenSet = TokenSet.create(KW_TRUE, KW_FALSE)

    /** Predefined symbols (null-like literals) — highlighted like booleans. */
    @JvmField val PREDEFINED: TokenSet = TokenSet.create(KW_NULL)

    /** Compile-time / preprocessor directives (#if, #elif, #else, #end). */
    @JvmField val PREPROCESSOR: TokenSet = TokenSet.create(
        KW_CT_IF, KW_CT_ELIF, KW_CT_ELSE, KW_CT_END
    )

    /** All numeric and string literal token types. */
    @JvmField val LITERALS: TokenSet = TokenSet.create(
        INT_LITERAL, FLOAT_LITERAL, HEX_LITERAL, BIN_LITERAL, OCT_LITERAL,
        STRING_LITERAL, CSTRING_LITERAL, CHAR_LITERAL
    )

    /** Comment token types (used by ParserDefinition). */
    @JvmField val COMMENTS: TokenSet = TokenSet.create(LINE_COMMENT, BLOCK_COMMENT)

    /** String-like token types (used by ParserDefinition). */
    @JvmField val STRING_LITERALS: TokenSet = TokenSet.create(
        STRING_LITERAL, CSTRING_LITERAL, CHAR_LITERAL
    )
}

package com.chic.highlighting

import com.chic.lexer.ChicTokenTypes
import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.Annotator
import com.intellij.lang.annotation.HighlightSeverity
import com.intellij.openapi.editor.DefaultLanguageHighlighterColors
import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.editor.colors.TextAttributesKey.createTextAttributesKey
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiWhiteSpace

/**
 * Rust-flavored semantic highlighting for Chic identifiers.
 *
 * Applied to each `IDENTIFIER` leaf, in priority order:
 *
 *   1. `@decorator`                                            → metadata
 *   2. Declaration site `<Name> : struct|enum|union|raw_union
 *                              |alias|extension|func`           → type / function decl
 *   3. After-dot `PascalCase`   (e.g. `FileMode.ReadOnly`)      → enum variant
 *   4. `UPPER_SNAKE_CASE`       (e.g. `MAX_BUFFER`)             → constant
 *   5. `PascalCase`             (e.g. `FileMode`, `Allocator`)  → type reference
 *
 * Anything else is left at its default lexer color (variables / functions /
 * parameters all share the editor scheme's default identifier color).
 *
 * No grammar / symbol table is required because the Chic parser produces a
 * flat PSI tree where every lexer token is a direct sibling of every other.
 */
class ChicSemanticAnnotator : Annotator {

    companion object {
        @JvmField
        val TYPE_DECLARATION = createTextAttributesKey(
            "CHIC_TYPE_DECLARATION", DefaultLanguageHighlighterColors.CLASS_NAME
        )

        @JvmField
        val FUNCTION_DECLARATION = createTextAttributesKey(
            "CHIC_FUNCTION_DECLARATION",
            DefaultLanguageHighlighterColors.FUNCTION_DECLARATION
        )

        @JvmField
        val TYPE_REFERENCE = createTextAttributesKey(
            "CHIC_TYPE_REFERENCE", DefaultLanguageHighlighterColors.CLASS_REFERENCE
        )

        @JvmField
        val ENUM_VARIANT = createTextAttributesKey(
            "CHIC_ENUM_VARIANT", DefaultLanguageHighlighterColors.STATIC_FIELD
        )

        @JvmField
        val CONSTANT = createTextAttributesKey(
            "CHIC_CONSTANT", DefaultLanguageHighlighterColors.CONSTANT
        )

        @JvmField
        val DECORATOR = createTextAttributesKey(
            "CHIC_DECORATOR", DefaultLanguageHighlighterColors.METADATA
        )

        private val TYPE_KEYWORDS = setOf(
            ChicTokenTypes.KW_STRUCT,
            ChicTokenTypes.KW_ENUM,
            ChicTokenTypes.KW_UNION,
            ChicTokenTypes.KW_RAW_UNION,
            ChicTokenTypes.KW_ALIAS,
            ChicTokenTypes.KW_EXTENSION,
        )
    }

    override fun annotate(element: PsiElement, holder: AnnotationHolder) {
        if (element.firstChild != null) return

        val attrs = when {
            element.tokenType == ChicTokenTypes.LINE_COMMENT -> ChicSyntaxHighlighter.LINE_COMMENT
            element.tokenType == ChicTokenTypes.BLOCK_COMMENT -> ChicSyntaxHighlighter.BLOCK_COMMENT
            element.tokenType in ChicTokenTypes.BOOLEANS -> ChicSyntaxHighlighter.BOOLEAN_LITERAL
            element.tokenType in ChicTokenTypes.PREDEFINED -> ChicSyntaxHighlighter.PREDEFINED
            element.tokenType in ChicTokenTypes.PREPROCESSOR -> ChicSyntaxHighlighter.PREPROCESSOR
            element.tokenType in ChicTokenTypes.KEYWORDS -> ChicSyntaxHighlighter.KEYWORD
            element.tokenType == ChicTokenTypes.BUILTIN_TYPE -> ChicSyntaxHighlighter.BUILTIN_TYPE
            element.tokenType == ChicTokenTypes.IDENTIFIER -> computeAttributes(element, element.text)
            else -> null
        } ?: return

        holder.newSilentAnnotation(HighlightSeverity.INFORMATION)
            .range(element.textRange)
            .textAttributes(attrs)
            .create()
    }

    private fun computeAttributes(element: PsiElement, text: String): TextAttributesKey? {
        // 1. @decorator (lexer emits the whole '@foo' as a single IDENTIFIER token)
        if (text.startsWith("@")) return DECORATOR

        // 2. Declaration site
        textBasedDeclarationAttribute(element)?.let { return it }
        declarationAttribute(element)?.let { return it }

        // 2.5 Generic target, e.g. extension<App>
        if (isInsideExtensionTarget(element)) return TYPE_REFERENCE

        // 3. Enum variant access: `.PascalCase` after a dot
        if (isPascalCase(text) && isAfterDot(element)) return ENUM_VARIANT

        // 4. UPPER_SNAKE_CASE constant
        if (isUpperSnakeCase(text)) return CONSTANT

        // 5. Plain PascalCase type reference
        if (isPascalCase(text)) return TYPE_REFERENCE

        return null
    }

    private fun textBasedDeclarationAttribute(element: PsiElement): TextAttributesKey? {
        val fileText = element.containingFile?.text ?: return null
        val after = fileText.substring(element.textRange.endOffset)
        val match = Regex("""^\s*:\s*([A-Za-z_][A-Za-z0-9_]*)""").find(after) ?: return null
        return when (match.groupValues[1]) {
            "struct", "enum", "union", "raw_union", "alias", "extension" -> TYPE_DECLARATION
            "func" -> FUNCTION_DECLARATION
            else -> null
        }
    }

    private fun isInsideExtensionTarget(element: PsiElement): Boolean {
        if (!isPascalCase(element.text)) return false

        val fileText = element.containingFile?.text ?: return false
        val start = element.textRange.startOffset
        val end = element.textRange.endOffset
        val before = fileText.substring(0, start).takeLast(32)
        val after = fileText.substring(end).take(32)

        return Regex("""extension\s*<\s*$""").containsMatchIn(before) &&
            Regex("""^\s*>""").containsMatchIn(after)
    }

    /** `<Name> : struct|enum|union|raw_union|alias|extension` → type, `: func` → function. */
    private fun declarationAttribute(element: PsiElement): TextAttributesKey? {
        val colon = nextMeaningfulSibling(element) ?: return null
        if (colon.tokenType != ChicTokenTypes.COLON) return null
        val kind = nextMeaningfulSibling(colon) ?: return null
        return when {
            kind.tokenType in TYPE_KEYWORDS         -> TYPE_DECLARATION
            kind.tokenType == ChicTokenTypes.KW_FUNC -> FUNCTION_DECLARATION
            else                                       -> null
        }
    }

    private fun isAfterDot(element: PsiElement): Boolean {
        val prev = previousMeaningfulSibling(element) ?: return false
        return prev.tokenType == ChicTokenTypes.DOT
    }

    /** Starts with uppercase letter and contains at least one lowercase letter. */
    private fun isPascalCase(s: String): Boolean {
        if (s.isEmpty() || !s[0].isUpperCase()) return false
        return s.any { it.isLowerCase() }
    }

    /** All uppercase letters/digits/underscores, length ≥ 2, contains at least one letter. */
    private fun isUpperSnakeCase(s: String): Boolean {
        if (s.length < 2) return false
        if (!s.all { it.isUpperCase() || it.isDigit() || it == '_' }) return false
        return s.any { it.isLetter() }
    }

    private fun nextMeaningfulSibling(element: PsiElement): PsiElement? {
        var sibling = element.nextSibling
        while (sibling != null) {
            val skip = sibling is PsiWhiteSpace ||
                (sibling.tokenType in ChicTokenTypes.COMMENTS)
            if (!skip) return sibling.takeIf { it.firstChild == null }
            sibling = sibling.nextSibling
        }
        return null
    }

    private fun previousMeaningfulSibling(element: PsiElement): PsiElement? {
        var sibling = element.prevSibling
        while (sibling != null) {
            val skip = sibling is PsiWhiteSpace ||
                (sibling.tokenType in ChicTokenTypes.COMMENTS)
            if (!skip) return sibling.takeIf { it.firstChild == null }
            sibling = sibling.prevSibling
        }
        return null
    }

    private val PsiElement.tokenType
        get() = node?.elementType
}

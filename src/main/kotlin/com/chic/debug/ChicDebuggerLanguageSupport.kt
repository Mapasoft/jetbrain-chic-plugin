package com.chic.debug

import com.intellij.openapi.editor.Document
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.TextRange
import com.intellij.xdebugger.evaluation.ExpressionInfo
import com.jetbrains.cidr.execution.debugger.CidrDebuggerLanguageSupport
import com.jetbrains.cidr.execution.debugger.CidrEvaluator
import com.jetbrains.cidr.execution.debugger.CidrStackFrame

class ChicDebuggerLanguageSupport : CidrDebuggerLanguageSupport() {
    override fun createEvaluator(frame: CidrStackFrame): CidrEvaluator =
        ChicEvaluator(frame)
}

private class ChicEvaluator(frame: CidrStackFrame) : CidrEvaluator(frame) {

    override fun getExpressionInfoAtOffset(
        project: Project,
        document: Document,
        offset: Int,
        sideEffectsAllowed: Boolean
    ): ExpressionInfo? {
        val text = document.charsSequence
        if (text.isEmpty()) return null

        val caret = offset.coerceIn(0, text.length - 1)
        val hit = when {
            isIdentifierPart(text[caret]) -> caret
            caret > 0 && isIdentifierPart(text[caret - 1]) -> caret - 1
            else -> return null
        }

        var start = hit
        while (start > 0 && isIdentifierPart(text[start - 1])) start--

        var end = hit + 1
        while (end < text.length && isIdentifierPart(text[end])) end++

        if (start == end || !isIdentifierStart(text[start])) return null

        val range = TextRange(start, end)
        val expression = text.subSequence(start, end).toString()
        return ExpressionInfo(range, expression, expression)
    }

    private fun isIdentifierStart(char: Char): Boolean =
        char == '_' || char.isLetter()

    private fun isIdentifierPart(char: Char): Boolean =
        char == '_' || char.isLetterOrDigit()
}

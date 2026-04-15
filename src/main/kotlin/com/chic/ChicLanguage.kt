package com.chic

import com.intellij.lang.Language

/** Singleton Language descriptor for Chic. */
class ChicLanguage private constructor() : Language("Chic") {
    companion object {
        @JvmField
        val INSTANCE: ChicLanguage = ChicLanguage()
    }
}

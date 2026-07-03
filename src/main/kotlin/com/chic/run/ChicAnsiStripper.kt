package com.chic.run

object ChicAnsiStripper {
    private val ansiPattern = Regex("""\u001B\[[0-?]*[ -/]*[@-~]""")

    fun strip(text: String): String =
        text.replace(ansiPattern, "")
}

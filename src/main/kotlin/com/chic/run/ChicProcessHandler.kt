package com.chic.run

import com.intellij.execution.configurations.GeneralCommandLine
import com.intellij.execution.process.OSProcessHandler
import com.intellij.openapi.util.Key

class ChicProcessHandler(commandLine: GeneralCommandLine) : OSProcessHandler(commandLine) {

    override fun notifyTextAvailable(text: String, outputType: Key<*>) {
        super.notifyTextAvailable(ChicAnsiStripper.strip(text), outputType)
    }
}

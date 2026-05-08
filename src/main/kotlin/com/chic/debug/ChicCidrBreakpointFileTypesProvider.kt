package com.chic.debug

import com.chic.ChicFileType
import com.intellij.openapi.fileTypes.FileType
import com.jetbrains.cidr.execution.debugger.breakpoints.CidrLineBreakpointFileTypesProvider

class ChicCidrBreakpointFileTypesProvider : CidrLineBreakpointFileTypesProvider {
    override fun getFileTypes(): Set<FileType> = setOf(ChicFileType.INSTANCE)
}

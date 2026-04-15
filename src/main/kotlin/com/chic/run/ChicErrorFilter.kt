package com.chic.run

import com.intellij.execution.filters.RegexpFilter
import com.intellij.openapi.project.Project

/**
 * Maps Chic compiler error lines to clickable hyperlinks in the Run console.
 *
 * Verified error format (from source/error.cpp):
 *
 *   filename:line:col error: message
 *
 * Example:
 *   src/main.chic:42:15 error: unexpected statement
 *
 * [RegexpFilter] recognises the `$FILE_PATH$`, `$LINE$`, and `$COLUMN$`
 * macros and automatically opens the corresponding source location when the
 * user clicks on the error line.
 */
class ChicErrorFilter(project: Project) : RegexpFilter(
    project,
    // Pattern:  <file>:<line>:<col> error: <message>
    "${RegexpFilter.FILE_PATH_MACROS}:${RegexpFilter.LINE_MACROS}:${RegexpFilter.COLUMN_MACROS} error:.*"
)

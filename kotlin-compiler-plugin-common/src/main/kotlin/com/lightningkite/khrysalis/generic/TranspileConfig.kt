package com.lightningkite.khrysalis.generic

import com.lightningkite.khrysalis.replacements.Replacements
import java.io.File

data class TranspileConfig(
    val replacements: Replacements,
    val projName: String,
    val outputFqnames: File,
    val outputDirectory: File,
    val commonPackage: String?,
    val libraryMode: Boolean,
)

//data class PreviousRunInfo(
//    val previouslyExistingFiles: List<>
//)
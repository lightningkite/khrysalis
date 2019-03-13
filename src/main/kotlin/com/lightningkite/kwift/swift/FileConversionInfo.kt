package com.lightningkite.kwift.swift

import com.fasterxml.jackson.annotation.JsonProperty

data class FileConversionInfo(
    val path: String = "",
    @JsonProperty("inputHash") val inputHash: Int = 0,
    @JsonProperty("outputHash") val outputHash: Int = 0,
    @JsonProperty("outputPath") val outputPath: String = ""
)

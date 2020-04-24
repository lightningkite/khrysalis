package com.lightningkite.khrysalis.ios.swift

import com.fasterxml.jackson.annotation.JsonProperty

data class FileConversionInfo(
    val path: String = "",
    @JsonProperty("inputHash") val inputHash: String = "",
    @JsonProperty("outputHash") val outputHash: String = "",
    @JsonProperty("outputPath") val outputPath: String = ""
)

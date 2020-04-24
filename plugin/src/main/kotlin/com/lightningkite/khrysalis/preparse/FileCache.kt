package com.lightningkite.khrysalis.preparse

data class FileCache(
    val path: String = "",
    val hash: String = "",
    val data: PreparseData = PreparseData()
)

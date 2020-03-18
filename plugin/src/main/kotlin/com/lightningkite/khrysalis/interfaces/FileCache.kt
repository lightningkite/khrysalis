package com.lightningkite.khrysalis.interfaces

import com.lightningkite.khrysalis.interfaces.InterfaceListener

data class FileCache(
    val path: String = "",
    val hash: String = "",
    val data: List<InterfaceListener.InterfaceData> = listOf()
)

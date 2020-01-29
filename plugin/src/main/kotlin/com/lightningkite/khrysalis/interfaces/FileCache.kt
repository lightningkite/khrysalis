package com.lightningkite.khrysalis.interfaces

import com.lightningkite.khrysalis.interfaces.InterfaceListener

data class FileCache(
    val path: String = "",
    val hash: Int = 0,
    val data: List<InterfaceListener.InterfaceData> = listOf()
)

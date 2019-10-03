package com.lightningkite.kwift.interfaces

import com.lightningkite.kwift.interfaces.InterfaceListener

data class FileCache(
    val path: String = "",
    val hash: Int = 0,
    val data: List<InterfaceListener.InterfaceData> = listOf()
)

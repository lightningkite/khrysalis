package com.lightningkite.kwift.views.shared

import com.lightningkite.kwift.observables.shared.ObservableStack

interface EntryPoint {
    fun handleDeepLink(schema: String, host: String, path: String, params: Map<String, String>){
        println("Empty handler")
    }
    fun onBackPressed(): Boolean = false
    val mainStack: ObservableStack<ViewGenerator>? get() = null
}

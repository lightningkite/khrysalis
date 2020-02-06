package com.lightningkite.khrysalis.views

import com.lightningkite.khrysalis.observables.ObservableStack

interface EntryPoint {
    fun handleDeepLink(schema: String, host: String, path: String, params: Map<String, String>){
        println("Empty handler; $schema://$host/$path/$params")
    }
    fun onBackPressed(): Boolean = false
    val mainStack: ObservableStack<ViewGenerator>? get() = null
}

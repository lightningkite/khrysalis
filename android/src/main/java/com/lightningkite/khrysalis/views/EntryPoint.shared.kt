package com.lightningkite.khrysalis.views

import com.lightningkite.khrysalis.observables.ObservableStack

interface EntryPoint {
    fun handleDeepLink(schema: String, host: String, path: String, params: Map<String, String>){
        println("Empty handler; $schema://$host/$path/$params")
    }

    /**
     * @return Whether or not to let the notification display.
     */
    fun handleNotificationInForeground(map: Map<String, String>): Boolean {
        println("Received notification in foreground with $map")
        return true
    }
    fun onBackPressed(): Boolean = false
    val mainStack: ObservableStack<ViewGenerator>? get() = null
}

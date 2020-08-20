package com.lightningkite.khrysalis.fcm

import com.lightningkite.khrysalis.SwiftMustBeClass
import com.lightningkite.khrysalis.observables.ObservableStack
import com.lightningkite.khrysalis.swiftMustBeClass

@SwiftMustBeClass
interface ForegroundNotificationHandler {
    fun handleNotificationInForeground(map: Map<String, String>): ForegroundNotificationHandlerResult {
        println("Received notification in foreground with $map")
        return ForegroundNotificationHandlerResult.SHOW_NOTIFICATION
    }
}

enum class ForegroundNotificationHandlerResult {
    SUPPRESS_NOTIFICATION, SHOW_NOTIFICATION, UNHANDLED
}

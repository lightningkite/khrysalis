"use strict";
// package com.lightningkite.khrysalis.fcm
//
// import com.lightningkite.khrysalis.observables.ObservableStack
// import com.lightningkite.khrysalis.swiftMustBeClass
//
// @swiftMustBeClass
// interface ForegroundNotificationHandler {
//     fun handleNotificationInForeground(map: Map<String, String>): ForegroundNotificationHandlerResult {
//     println("Received notification in foreground with $map")
//     return ForegroundNotificationHandlerResult.SHOW_NOTIFICATION
// }
// }
//
// enum class ForegroundNotificationHandlerResult {
//     SUPPRESS_NOTIFICATION, SHOW_NOTIFICATION, UNHANDLED
// }
Object.defineProperty(exports, "__esModule", { value: true });
//! Declares com.lightningkite.khrysalis.fcm.ForegroundNotificationHandlerResult
class ForegroundNotificationHandlerResult {
    constructor(name) {
        this.name = name;
    }
    static values() { return ForegroundNotificationHandlerResult._values; }
    static valueOf(name) { return ForegroundNotificationHandlerResult[name]; }
    toString() { return this.name; }
    toJSON() { return this.name; }
}
exports.ForegroundNotificationHandlerResult = ForegroundNotificationHandlerResult;
ForegroundNotificationHandlerResult.SUPPRESS_NOTIFICATION = new ForegroundNotificationHandlerResult("SUPPRESS_NOTIFICATION");
ForegroundNotificationHandlerResult.SHOW_NOTIFICATION = new ForegroundNotificationHandlerResult("SHOW_NOTIFICATION");
ForegroundNotificationHandlerResult.UNHANDLED = new ForegroundNotificationHandlerResult("UNHANDLED");
ForegroundNotificationHandlerResult._values = [ForegroundNotificationHandlerResult.SUPPRESS_NOTIFICATION, ForegroundNotificationHandlerResult.SHOW_NOTIFICATION, ForegroundNotificationHandlerResult.UNHANDLED];
//# sourceMappingURL=ForegroundNotificationHandler.shared.js.map
"use strict";
// Generated by Khrysalis TypeScript converter - this file will be overwritten.
// File: ForegroundNotificationHandler.shared.kt
// Package: com.lightningkite.khrysalis.fcm
Object.defineProperty(exports, "__esModule", { value: true });
var ForegroundNotificationHandlerDefaults;
(function (ForegroundNotificationHandlerDefaults) {
    function handleNotificationInForeground(this_, map) {
        console.log(`Received notification in foreground with ${map}`);
        return ForegroundNotificationHandlerResult.SHOW_NOTIFICATION;
    }
    ForegroundNotificationHandlerDefaults.handleNotificationInForeground = handleNotificationInForeground;
})(ForegroundNotificationHandlerDefaults = exports.ForegroundNotificationHandlerDefaults || (exports.ForegroundNotificationHandlerDefaults = {}));
//! Declares com.lightningkite.khrysalis.fcm.ForegroundNotificationHandlerResult
class ForegroundNotificationHandlerResult {
    constructor(name, jsonName) {
        this.name = name;
        this.jsonName = jsonName;
    }
    static values() { return ForegroundNotificationHandlerResult._values; }
    static valueOf(name) { return ForegroundNotificationHandlerResult[name]; }
    toString() { return this.name; }
    toJSON() { return this.jsonName; }
}
exports.ForegroundNotificationHandlerResult = ForegroundNotificationHandlerResult;
ForegroundNotificationHandlerResult.SUPPRESS_NOTIFICATION = new ForegroundNotificationHandlerResult("SUPPRESS_NOTIFICATION", "SUPPRESS_NOTIFICATION");
ForegroundNotificationHandlerResult.SHOW_NOTIFICATION = new ForegroundNotificationHandlerResult("SHOW_NOTIFICATION", "SHOW_NOTIFICATION");
ForegroundNotificationHandlerResult.UNHANDLED = new ForegroundNotificationHandlerResult("UNHANDLED", "UNHANDLED");
ForegroundNotificationHandlerResult._values = [ForegroundNotificationHandlerResult.SUPPRESS_NOTIFICATION, ForegroundNotificationHandlerResult.SHOW_NOTIFICATION, ForegroundNotificationHandlerResult.UNHANDLED];
//# sourceMappingURL=ForegroundNotificationHandler.shared.js.map
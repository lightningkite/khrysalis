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

//! Declares com.lightningkite.khrysalis.fcm.ForegroundNotificationHandler
export interface ForegroundNotificationHandler {
    handleNotificationInForeground(map: Map<string, string>): ForegroundNotificationHandlerResult;
}

//! Declares com.lightningkite.khrysalis.fcm.ForegroundNotificationHandlerResult
export class ForegroundNotificationHandlerResult {
    private constructor(name: string) {
        this.name = name;
    }

    public static SUPPRESS_NOTIFICATION = new ForegroundNotificationHandlerResult("SUPPRESS_NOTIFICATION");
    public static SHOW_NOTIFICATION = new ForegroundNotificationHandlerResult("SHOW_NOTIFICATION");
    public static UNHANDLED = new ForegroundNotificationHandlerResult("UNHANDLED");

    private static _values: Array<ForegroundNotificationHandlerResult> = [ForegroundNotificationHandlerResult.SUPPRESS_NOTIFICATION, ForegroundNotificationHandlerResult.SHOW_NOTIFICATION, ForegroundNotificationHandlerResult.UNHANDLED];
    public static values(): Array<ForegroundNotificationHandlerResult> { return ForegroundNotificationHandlerResult._values; }
    public readonly name: string;
    public static valueOf(name: string): ForegroundNotificationHandlerResult { return (ForegroundNotificationHandlerResult as any)[name]; }
    public toString(): string { return this.name }
    public toJSON(): string { return this.name }
}
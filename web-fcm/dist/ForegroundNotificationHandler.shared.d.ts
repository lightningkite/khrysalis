export interface ForegroundNotificationHandler {
    handleNotificationInForeground(map: Map<string, string>): ForegroundNotificationHandlerResult;
}
export declare namespace ForegroundNotificationHandlerDefaults {
    function handleNotificationInForeground(this_: ForegroundNotificationHandler, map: Map<string, string>): ForegroundNotificationHandlerResult;
}
export declare class ForegroundNotificationHandlerResult {
    private constructor();
    static SUPPRESS_NOTIFICATION: ForegroundNotificationHandlerResult;
    static SHOW_NOTIFICATION: ForegroundNotificationHandlerResult;
    static UNHANDLED: ForegroundNotificationHandlerResult;
    private static _values;
    static values(): Array<ForegroundNotificationHandlerResult>;
    readonly name: string;
    readonly jsonName: string;
    static valueOf(name: string): ForegroundNotificationHandlerResult;
    toString(): string;
    toJSON(): string;
}

// Generated by Khrysalis TypeScript converter
// File: time/TimeZone.actual.kt
// Package: com.lightningkite.khrysalis.time

/* SHARED DECLARATIONS
class TimeZone {

    val id: String
    val displayName: String
    fun getOffset(date: Long): Int

    companion object {
        fun getDefault(): TimeZone {}
    }
}
 */

export class TimeZone {
    public readonly id: string;
    public readonly displayName: string;
    public readonly getOffset: (date: number) => number;

    public constructor(id: string, displayName: string, getOffset: (date: number) => number) {
        this.id = id;
        this.displayName = displayName;
        this.getOffset = getOffset;
    }

    public static Companion = class Companion {
        public static INSTANCE = new Companion();
        getDefault(): TimeZone {
            return new TimeZone(
                "local",
                new Date().toLocaleString(undefined, {
                    timeZoneName: "long"
                }),
                (date: number) => {
                    return new Date(date).getTimezoneOffset() * 1000;
                }
            );
        }
    }
}
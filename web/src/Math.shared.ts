// Generated by Khrysalis TypeScript converter - this file will be overwritten.
// File: Math.shared.kt
// Package: com.lightningkite.khrysalis

//! Declares com.lightningkite.khrysalis.floorMod>kotlin.Int
export function kotlinIntFloorMod(this_: number, other: number): number { 
    return (this_ % other + other) % other; 
}
//! Declares com.lightningkite.khrysalis.floorDiv>kotlin.Int
export function kotlinIntFloorDiv(this_: number, other: number): number {
    if (this_ < 0) {
        return this_ / other - 1;
    } else {
        return this_ / other;
    }
}

//! Declares com.lightningkite.khrysalis.floorMod>kotlin.Float
export function kotlinFloatFloorMod(this_: number, other: number): number { 
    return (this_ % other + other) % other; 
}
//! Declares com.lightningkite.khrysalis.floorDiv>kotlin.Float
export function kotlinFloatFloorDiv(this_: number, other: number): number {
    if (this_ < 0) {
        return this_ / other - 1;
    } else {
        return this_ / other;
    }
}


//! Declares com.lightningkite.khrysalis.floorMod>kotlin.Double
export function kotlinDoubleFloorMod(this_: number, other: number): number { 
    return (this_ % other + other) % other; 
}
//! Declares com.lightningkite.khrysalis.floorDiv>kotlin.Double
export function kotlinDoubleFloorDiv(this_: number, other: number): number {
    if (this_ < 0) {
        return this_ / other - 1;
    } else {
        return this_ / other;
    }
}


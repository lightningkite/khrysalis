"use strict";
// Generated by Khrysalis TypeScript converter - this file will be overwritten.
// File: observables/Closeable.actual.kt
// Package: com.lightningkite.khrysalis.observables
// FQImport: com.lightningkite.khrysalis.observables.Closeable TS CloseableDefaults
// FQImport: com.lightningkite.khrysalis.observables.Closeable.close TS close
// FQImport: com.lightningkite.khrysalis.observables.Close.closer TS closer
// FQImport: com.lightningkite.khrysalis.observables.Close.disposed TS disposed
// FQImport: com.lightningkite.khrysalis.observables.Closeable TS Closeable
Object.defineProperty(exports, "__esModule", { value: true });
class CloseableDefaults {
    static isDisposed(this_) {
        return false;
    }
    static dispose(this_) {
        this_.close();
    }
}
exports.CloseableDefaults = CloseableDefaults;
//! Declares com.lightningkite.khrysalis.observables.Close
class Close {
    constructor(closer) {
        this.closer = closer;
        this.disposed = false;
    }
    isDisposed() {
        return this.disposed;
    }
    close() {
        this.disposed = true;
        this.closer();
    }
    dispose() { return CloseableDefaults.dispose(this); }
}
exports.Close = Close;
Close.implementsInterfaceComLightningkiteKhrysalisObservablesCloseable = true;
Close.implementsInterfaceIoReactivexDisposablesDisposable = true;

// Generated by Khrysalis TypeScript converter - this file will be overwritten.
// File: observables/Event.actual.kt
// Package: com.lightningkite.khrysalis.observables
// FQImport: com.lightningkite.khrysalis.observables.invokeAll.Element TS Element
// FQImport: com.lightningkite.khrysalis.observables.invokeAll.value TS value
import { Observer } from 'rxjs'

//! Declares com.lightningkite.khrysalis.observables.invokeAll
export function ioReactivexObserverInvokeAll<Element>(this_: Observer<Element>, value: Element): void{ return this_.next(value); }

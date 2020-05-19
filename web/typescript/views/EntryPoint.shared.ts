// Generated by Khrysalis TypeScript converter - this file will be overwritten.
// File: views/EntryPoint.shared.kt
// Package: com.lightningkite.khrysalis.views
// FQImport: com.lightningkite.khrysalis.views.EntryPoint.handleDeepLink.schema TS schema
// FQImport: kotlin.Boolean TS Boolean
// FQImport: com.lightningkite.khrysalis.views.ViewGenerator TS ViewGenerator
// FQImport: com.lightningkite.khrysalis.views.EntryPoint.handleDeepLink.host TS host
// FQImport: com.lightningkite.khrysalis.views.EntryPoint.handleDeepLink.params TS params
// FQImport: kotlin.collections.Map TS Map
// FQImport: com.lightningkite.khrysalis.observables.ObservableStack TS ObservableStack
// FQImport: com.lightningkite.khrysalis.views.EntryPoint.handleDeepLink.path TS path
import { ViewGenerator } from './ViewGenerator.shared'
import { ObservableStack } from './../observables/ObservableStack.shared'

//! Declares com.lightningkite.khrysalis.views.EntryPoint
export interface EntryPoint {
    
    handleDeepLink(schema: string, host: string, path: string, params: Map<string, string>): void
    onBackPressed(): Boolean
    readonly mainStack: (ObservableStack<ViewGenerator> | null);
    
}
export class EntryPointDefaults {
    public static handleDeepLink(this_, schema: string, host: string, path: string, params: Map<string, string>): void{
        console.log(`Empty handler; ${schema}://${host}/${path}/${params}`);
    }
    public static onBackPressed(this_): Boolean{ return false; }
    public static getMainStack(this_: EntryPoint){ return null; }
}


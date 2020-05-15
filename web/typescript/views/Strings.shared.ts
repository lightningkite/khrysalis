// Generated by Khrysalis TypeScript converter - this file will be overwritten.
// File: views/Strings.shared.kt
// Package: com.lightningkite.khrysalis.views
// FQImport: com.lightningkite.khrysalis.views.ViewStringList.parts TS parts
// FQImport: com.lightningkite.khrysalis.views.ViewString SKIPPED due to same file
// FQImport: com.lightningkite.khrysalis.views.ViewStringTemplate.get.dependency TS dependency
// FQImport: com.lightningkite.khrysalis.views.ViewStringComplex.get.dependency TS dependency
// FQImport: com.lightningkite.khrysalis.views.toDebugString SKIPPED due to same file
// FQImport: com.lightningkite.khrysalis.views.toDebugString TS comLightningkiteKhrysalisViewsViewStringToDebugString
// FQImport: com.lightningkite.khrysalis.views.ViewString.get TS get
// FQImport: com.lightningkite.khrysalis.views.ViewStringResource TS ViewStringResource
// FQImport: kotlin.Int.toString TS toString
// FQImport: com.lightningkite.khrysalis.views.StringResource TS StringResource
// FQImport: com.lightningkite.khrysalis.views.ViewStringList.get.dependency TS dependency
// FQImport: com.lightningkite.khrysalis.views.ViewStringTemplate SKIPPED due to same file
// FQImport: com.lightningkite.khrysalis.views.ViewStringResource SKIPPED due to same file
// FQImport: com.lightningkite.khrysalis.views.getString TS comLightningkiteKhrysalisAndroidActivityAccessGetString
// FQImport: com.lightningkite.khrysalis.views.ViewStringTemplate.get.fixedArguments TS fixedArguments
// FQImport: com.lightningkite.khrysalis.views.ViewStringRaw TS ViewStringRaw
// FQImport: com.lightningkite.khrysalis.views.ViewStringList.separator TS separator
// FQImport: com.lightningkite.khrysalis.views.toDebugString.thing TS thing
// FQImport: com.lightningkite.khrysalis.views.ViewStringRaw SKIPPED due to same file
// FQImport: kotlin.collections.List.size TS size
// FQImport: com.lightningkite.khrysalis.views.ViewStringResource.resource TS resource
// FQImport: com.lightningkite.khrysalis.views.ViewDependency TS ViewDependency
// FQImport: com.lightningkite.khrysalis.views.ViewStringTemplate.get.<anonymous>.it TS it
// FQImport: com.lightningkite.khrysalis.views.ViewStringComplex TS ViewStringComplex
// FQImport: com.lightningkite.khrysalis.views.ViewStringComplex SKIPPED due to same file
// FQImport: kotlin.collections.joinToString TS kotlinCollectionsIterableJoinToString
// FQImport: com.lightningkite.khrysalis.views.joinToViewString.separator TS separator
// FQImport: com.lightningkite.khrysalis.views.ViewStringTemplate.arguments TS arguments
// FQImport: com.lightningkite.khrysalis.views.ViewStringList.get.<anonymous>.it TS it
// FQImport: com.lightningkite.khrysalis.views.ViewStringRaw.string TS string
// FQImport: com.lightningkite.khrysalis.views.ViewStringList TS ViewStringList
// FQImport: com.lightningkite.khrysalis.views.ViewStringResource.get.dependency TS dependency
// FQImport: com.lightningkite.khrysalis.views.ViewStringTemplate.template TS template
// FQImport: com.lightningkite.khrysalis.views.ViewStringTemplate TS ViewStringTemplate
// FQImport: com.lightningkite.khrysalis.views.toDebugString.<anonymous>.it TS it
// FQImport: com.lightningkite.khrysalis.views.ViewString TS ViewString
// FQImport: com.lightningkite.khrysalis.views.ViewStringTemplate.get.templateResolved TS templateResolved
// FQImport: com.lightningkite.khrysalis.views.ViewStringComplex.getter TS getter
// FQImport: com.lightningkite.khrysalis.views.ViewStringList SKIPPED due to same file
import { ViewDependency, comLightningkiteKhrysalisAndroidActivityAccessGetString } from './ViewDependency.actual'
import { StringResource } from './ResourceTypes.actual'
import { vsprintf } from 'sprintf'
import { checkIsInterface, tryCastInterface } from 'khrysalis/dist/Kotlin'

//! Declares com.lightningkite.khrysalis.views.ViewString
export interface ViewString {
    
    get(dependency: ViewDependency): string
}
export class ViewStringDefaults {
    public static get(this_Get: ViewString, dependency: ViewDependency){ return ; }
}

//! Declares com.lightningkite.khrysalis.views.ViewStringRaw
export class ViewStringRaw implements ViewString {
    public static implementsInterfaceComLightningkiteKhrysalisViewsViewString = true;
    public readonly string: string;
    public constructor( string: string) {
        this.string = string;
    }
    
    public get(dependency: ViewDependency): string{ return this.string; }
}

//! Declares com.lightningkite.khrysalis.views.ViewStringResource
export class ViewStringResource implements ViewString {
    public static implementsInterfaceComLightningkiteKhrysalisViewsViewString = true;
    public readonly resource: StringResource;
    public constructor( resource: StringResource) {
        this.resource = resource;
    }
    
    public get(dependency: ViewDependency): string{ return comLightningkiteKhrysalisAndroidActivityAccessGetString(dependency, this.resource); }
}

//! Declares com.lightningkite.khrysalis.views.ViewStringTemplate
export class ViewStringTemplate implements ViewString {
    public static implementsInterfaceComLightningkiteKhrysalisViewsViewString = true;
    public readonly template: ViewString;
    public readonly arguments: Array<any>;
    public constructor( template: ViewString,  arguments: Array<any>) {
        this.template = template;
        this.arguments = arguments;
    }
    
    public get(dependency: ViewDependency): string{
        const templateResolved = this.template.get(dependency);
        
        const fixedArguments = this.arguments.map((it) => (tryCastInterface(it, "ComLightningkiteKhrysalisViewsViewString"))?.get(dependency) ?: it);
        
        return vsprintf(templateResolved, fixedArguments);
    }
}

//! Declares com.lightningkite.khrysalis.views.ViewStringComplex
export class ViewStringComplex implements ViewString {
    public static implementsInterfaceComLightningkiteKhrysalisViewsViewString = true;
    public readonly getter:  (a: ViewDependency) => string;
    public constructor( getter:  (a: ViewDependency) => string) {
        this.getter = getter;
    }
    
    public get(dependency: ViewDependency): string{ return this.getter(dependency); }
}

//! Declares com.lightningkite.khrysalis.views.ViewStringList
export class ViewStringList implements ViewString {
    public static implementsInterfaceComLightningkiteKhrysalisViewsViewString = true;
    public readonly parts: Array<ViewString>;
    public readonly separator: string;
    public constructor( parts: Array<ViewString>,  separator: string = `\n`) {
        this.parts = parts;
        this.separator = separator;
    }
    
    public get(dependency: ViewDependency): string{
        return kotlinCollectionsIterableJoinToString(this.parts, this.separator, undefined, undefined, undefined, undefined, (it) => it.get(dependency));
    }
}

//! Declares com.lightningkite.khrysalis.views.joinToViewString
export function kotlinCollectionsListJoinToViewString(this_JoinToViewString: Array< ViewString>, separator: string = `\n`): ViewString{
    (() => {if(this_JoinToViewString.size === 1){
                return this_JoinToViewString[0];
    }})()
    return new ViewStringList(this_JoinToViewString, separator);
}

//! Declares com.lightningkite.khrysalis.views.toDebugString
export function comLightningkiteKhrysalisViewsViewStringToDebugString(this_ToDebugString: ViewString): string{
    const thing = this_ToDebugString;
    
    (() => {if(thing instanceof ViewStringRaw){
                return return thing.string
            }else if(thing instanceof ViewStringResource){
                return return thing.resource.toString()
            }else if(thing instanceof ViewStringTemplate){
                return return comLightningkiteKhrysalisViewsViewStringToDebugString(thing.template) + "(" + kotlinCollectionsIterableJoinToString(thing.arguments, undefined, undefined, undefined, undefined, undefined, (it) => checkIsInterface(it, "ComLightningkiteKhrysalisViewsViewString") ? return comLightningkiteKhrysalisViewsViewStringToDebugString(it) : return `${it}`) + ")"
            }else if(thing instanceof ViewStringList){
                return return kotlinCollectionsIterableJoinToString(thing.parts, thing.separator, undefined, undefined, undefined, undefined, (it) => comLightningkiteKhrysalisViewsViewStringToDebugString(it))
            }else if(thing instanceof ViewStringComplex){
                return return `<Complex string ${thing}>`
            }else {
                return return "Unknown"
    }})();
}


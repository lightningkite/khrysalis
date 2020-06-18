// Generated by Khrysalis TypeScript converter - this file will be overwritten.
// File: views/DjangoErrorTranslator.shared.kt
// Package: com.lightningkite.khrysalis.views
// FQImport: com.lightningkite.khrysalis.views.DjangoErrorTranslator.parseError.code TS code
// FQImport: com.lightningkite.khrysalis.views.DjangoErrorTranslator.wrapNoResponse.<anonymous>.error TS error
// FQImport: kotlin.text.StringBuilder TS StringBuilder
// FQImport: kotlin.collections.Map TS Map
// FQImport: com.lightningkite.khrysalis.views.DjangoErrorTranslator.handleNode.builder TS builder
// FQImport: com.lightningkite.khrysalis.views.DjangoErrorTranslator.parseError.builder TS builder
// FQImport: com.lightningkite.khrysalis.views.DjangoErrorTranslator.handleNode.node TS node
// FQImport: kotlin.text.isUpperCase>kotlin.Char TS kotlinCharIsUpperCase
// FQImport: com.lightningkite.khrysalis.views.DjangoErrorTranslator.wrap.<anonymous>.result TS result
// FQImport: com.lightningkite.khrysalis.views.ViewStringResource TS ViewStringResource
// FQImport: com.lightningkite.khrysalis.views.DjangoErrorTranslator.parseError.error TS error
// FQImport: com.lightningkite.khrysalis.views.DjangoErrorTranslator.parseError.errorJson TS errorJson
// FQImport: com.lightningkite.khrysalis.views.DjangoErrorTranslator.wrap.<anonymous>.code TS code
// FQImport: com.lightningkite.khrysalis.views.DjangoErrorTranslator.serverErrorResource TS serverErrorResource
// FQImport: com.lightningkite.khrysalis.views.ViewStringRaw TS ViewStringRaw
// FQImport: com.lightningkite.khrysalis.views.DjangoErrorTranslator.wrapNoResponse.callback TS callback
// FQImport: com.lightningkite.khrysalis.views.DjangoErrorTranslator.otherErrorResource TS otherErrorResource
// FQImport: com.lightningkite.khrysalis.views.DjangoErrorTranslator.wrap.callback TS callback
// FQImport: com.lightningkite.khrysalis.views.DjangoErrorTranslator.wrap.T TS T
// FQImport: com.lightningkite.khrysalis.views.DjangoErrorTranslator.wrap.<anonymous>.error TS error
// FQImport: com.lightningkite.khrysalis.views.DjangoErrorTranslator.handleNode.value TS value
// FQImport: com.lightningkite.khrysalis.views.DjangoErrorTranslator.wrapNoResponse.<anonymous>.code TS code
// FQImport: com.lightningkite.khrysalis.views.DjangoErrorTranslator.parseError TS parseError
// FQImport: com.lightningkite.khrysalis.views.ViewString TS ViewString
// FQImport: com.lightningkite.khrysalis.views.DjangoErrorTranslator.parseError.resultError TS resultError
// FQImport: java.lang.StringBuilder.toString TS toString
// FQImport: com.lightningkite.khrysalis.views.DjangoErrorTranslator.connectivityErrorResource TS connectivityErrorResource
// FQImport: com.lightningkite.khrysalis.fromJsonStringUntyped>kotlin.String TS kotlinStringFromJsonStringUntyped
// FQImport: com.lightningkite.khrysalis.views.DjangoErrorTranslator.handleNode TS handleNode
import { ViewString, ViewStringRaw, ViewStringResource } from './Strings.shared'
import { StringBuilder } from '../kotlin/kotlin.text'
import { kotlinCharIsUpperCase } from '../kotlin/kotlin.text'
import { kotlinStringFromJsonStringUntyped } from '../Codable.actual'
import { checkIsInterface } from '../Kotlin'

//! Declares com.lightningkite.khrysalis.views.DjangoErrorTranslator
export class DjangoErrorTranslator {
    public readonly connectivityErrorResource: string;
    public readonly serverErrorResource: string;
    public readonly otherErrorResource: string;
    public constructor(connectivityErrorResource: string, serverErrorResource: string, otherErrorResource: string) {
        this.connectivityErrorResource = connectivityErrorResource;
        this.serverErrorResource = serverErrorResource;
        this.otherErrorResource = otherErrorResource;
    }
    
    
    public handleNode(builder: StringBuilder, node: (any | null)): void {
        if (node.equals(null)) return;
        if (checkIsInterface<Map<any, any>>(node, "KotlinCollectionsMap")){
            for (const toDestructure of node) {
                const key = toDestructure[0]
                const value = toDestructure[1]
                
                this.handleNode(builder, value)
                
            }
        } else if (checkIsInterface<Array<any>>(node, "KotlinCollectionsList")){
            for (const value of node) {
                this.handleNode(builder, value);
            }
        } else if (typeof (node) == "string"){
            //Rough check for human-readability - sentences start with uppercase and will have spaces
            if (node !== "" && kotlinCharIsUpperCase(node[0]) && (node.indexOf(" ") != -1)) {
                builder.value += node + '\n';
            }
        }
    }
    public parseError(code: number, error: (string | null)): (ViewString | null) {
        let resultError: (ViewString | null) = null;
        
        switch(code / 100) {
            case 0:
            resultError = new ViewStringResource(this.connectivityErrorResource)
            break;
            case 1:
            case 2:
            case 3:
            
            break;
            case 4:
            const errorJson = ((_it)=>{
                    if(_it === null) return null;
                    return kotlinStringFromJsonStringUntyped(_it)
            })(error);
            
            if (!(errorJson.equals(null))) {
                const builder = new StringBuilder();
                
                this.handleNode(builder, errorJson);
                resultError = new ViewStringRaw(builder.toString());
            } else {
                resultError = new ViewStringRaw(error ?? "");
            }
            break;
            case 5:
            resultError = new ViewStringResource(this.serverErrorResource)
            break;
            default:
            resultError = new ViewStringResource(this.otherErrorResource)
            break;
        }
        
        return resultError;
    }
    
    public wrap<T>(callback:  (result: (T | null), error: (ViewString | null)) => void): (code: number, result: (T | null), error: (string | null)) => void {
        return (code, result, error) => {
            callback(result, this.parseError(code, error))
        };
    }
    
    public wrapNoResponse(callback:  (error: (ViewString | null)) => void): (code: number, error: (string | null)) => void {
        return (code, error) => {
            callback(this.parseError(code, error))
        };
    }
    
}

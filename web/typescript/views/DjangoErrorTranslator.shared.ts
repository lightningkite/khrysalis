// Generated by Khrysalis TypeScript converter - this file will be overwritten.
// File: views/DjangoErrorTranslator.shared.kt
// Package: com.lightningkite.khrysalis.views
// FQImport: com.lightningkite.khrysalis.views.DjangoErrorTranslator.parseError.code TS code
// FQImport: com.lightningkite.khrysalis.views.DjangoErrorTranslator.wrapNoResponse.<anonymous>.error TS error
// FQImport: kotlin.text.StringBuilder TS StringBuilder
// FQImport: com.lightningkite.khrysalis.views.DjangoErrorTranslator.handleNode.builder TS builder
// FQImport: com.lightningkite.khrysalis.views.DjangoErrorTranslator.parseError.builder TS builder
// FQImport: com.lightningkite.khrysalis.views.DjangoErrorTranslator.handleNode.node TS node
// FQImport: com.lightningkite.khrysalis.views.DjangoErrorTranslator.wrap.<anonymous>.result TS result
// FQImport: com.lightningkite.khrysalis.views.ViewStringResource TS ViewStringResource
// FQImport: com.lightningkite.khrysalis.views.StringResource TS StringResource
// FQImport: kotlin.text.isUpperCase TS kotlinCharIsUpperCase
// FQImport: com.lightningkite.khrysalis.views.DjangoErrorTranslator.parseError.error TS error
// FQImport: com.lightningkite.khrysalis.views.DjangoErrorTranslator.parseError.errorJson TS errorJson
// FQImport: com.lightningkite.khrysalis.views.DjangoErrorTranslator.wrap.<anonymous>.code TS code
// FQImport: com.lightningkite.khrysalis.views.DjangoErrorTranslator.serverErrorResource TS serverErrorResource
// FQImport: com.lightningkite.khrysalis.views.ViewStringRaw TS ViewStringRaw
// FQImport: com.lightningkite.khrysalis.views.DjangoErrorTranslator.wrapNoResponse.callback TS callback
// FQImport: kotlin.text.contains TS kotlinCharSequenceContains
// FQImport: com.lightningkite.khrysalis.views.DjangoErrorTranslator.otherErrorResource TS otherErrorResource
// FQImport: com.lightningkite.khrysalis.views.DjangoErrorTranslator.wrap.callback TS callback
// FQImport: com.lightningkite.khrysalis.views.DjangoErrorTranslator.wrap.T TS T
// FQImport: com.lightningkite.khrysalis.views.DjangoErrorTranslator.wrap.<anonymous>.error TS error
// FQImport: com.lightningkite.khrysalis.views.DjangoErrorTranslator.handleNode.value TS value
// FQImport: com.lightningkite.khrysalis.views.DjangoErrorTranslator.wrapNoResponse.<anonymous>.code TS code
// FQImport: com.lightningkite.khrysalis.views.DjangoErrorTranslator.parseError TS parseError
// FQImport: com.lightningkite.khrysalis.views.ViewString TS ViewString
// FQImport: com.lightningkite.khrysalis.views.DjangoErrorTranslator.parseError.resultError TS resultError
// FQImport: com.lightningkite.khrysalis.fromJsonStringUntyped TS kotlinStringFromJsonStringUntyped
// FQImport: java.lang.StringBuilder.toString TS toString
// FQImport: com.lightningkite.khrysalis.views.DjangoErrorTranslator.connectivityErrorResource TS connectivityErrorResource
// FQImport: com.lightningkite.khrysalis.views.DjangoErrorTranslator.handleNode TS handleNode
import { StringBuilder, kotlinCharIsUpperCase } from './../kotlin/kotlin.text'
import { kotlinStringFromJsonStringUntyped } from './../Codable.actual'
import { ViewString, ViewStringRaw, ViewStringResource } from './Strings.shared'
import { StringBuilder } from 'khrysalis/dist/kotlin/kotlin.text'
import { StringResource } from './ResourceTypes.actual'
import { checkIsInterface } from 'khrysalis/dist/Kotlin'

//! Declares com.lightningkite.khrysalis.views.DjangoErrorTranslator
export class DjangoErrorTranslator {
    public readonly connectivityErrorResource: StringResource;
    public readonly serverErrorResource: StringResource;
    public readonly otherErrorResource: StringResource;
    public constructor( connectivityErrorResource: StringResource,  serverErrorResource: StringResource,  otherErrorResource: StringResource) {
        this.connectivityErrorResource = connectivityErrorResource;
        this.serverErrorResource = serverErrorResource;
        this.otherErrorResource = otherErrorResource;
    }
    
    
    public handleNode(builder: StringBuilder, node: (any | null)){
        node.equals(null) ? return : 
        (() => {if(checkIsInterface(node, "KotlinCollectionsMap")){
                    for (const toDestructure of node) {
                        const key = toDestructure[0]
                        const value = toDestructure[1]
                        
                        handleNode(builder, value)
                        
                    }
                }else if(checkIsInterface(node, "KotlinCollectionsList")){
                    for (const value of node) {
                        handleNode(builder, value);
                    }
                }else if(typeof (node) == "string"){
                    //Rough check for human-readability - sentences start with uppercase and will have spaces
                    (() => {if(node !== "" && kotlinCharIsUpperCase(node[0]) && kotlinCharSequenceContains(node, " ", undefined)) {
                                return builder.value += '\n';
                    }})()
        }})();
    }
    public parseError(code: number, error: (string | null)): (ViewString | null){
        let resultError: (ViewString | null) = null;
        
        (() => {switch(code / 100){
                    case 0:
                    return resultError = new ViewStringResource(this.connectivityErrorResource)
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
                    
                    return (() => {if(!(errorJson.equals(null))){
                                const builder = StringBuilder();
                                
                                handleNode(builder, errorJson);
                                resultError = new ViewStringRaw(builder.toString());
                            } else {
                                resultError = new ViewStringRaw(error ?: "");
                    }})()
                    break;
                    case 5:
                    return resultError = new ViewStringResource(this.serverErrorResource)
                    break;
                    default:
                    return resultError = new ViewStringResource(this.otherErrorResource)
                    break;
                }
        })();
        return resultError;
    }
    
    public wrap<T>(
        callback:  (result: (T | null), error: (ViewString | null)) => void
    ): (code: number, result: (T | null), error: (string | null)) => void{
        return (code, result, error) => this.callback(result, this.parseError(code, error));
    }
    
    public wrapNoResponse(
        callback:  (error: (ViewString | null)) => void
    ): (code: number, error: (string | null)) => void{
        return (code, error) => this.callback(this.parseError(code, error));
    }
    
}


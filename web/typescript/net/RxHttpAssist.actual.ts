// Generated by Khrysalis TypeScript converter - this file will be overwritten.
// File: net/RxHttpAssist.actual.kt
// Package: com.lightningkite.khrysalis.net
// FQImport: com.lightningkite.khrysalis.net.readJson.<anonymous>.it TS it
// FQImport: com.lightningkite.khrysalis.net.HttpResponseException TS HttpResponseException
// FQImport: com.fasterxml.jackson.module.kotlin.jacksonTypeRef TS jacksonTypeRef
// FQImport: com.lightningkite.khrysalis.net.unsuccessfulAsError.<anonymous>.it TS it
// FQImport: com.lightningkite.khrysalis.net.readJson.T TS T
// FQImport: com.lightningkite.khrysalis.net.readJson TS okhttp3ResponseReadJson
// FQImport: com.lightningkite.khrysalis.net.HttpResponse TS HttpResponse
// FQImport: isSuccessful TS getOkhttp3ResponseIsSuccessful
// FQImport: com.lightningkite.khrysalis.net.readJson.typeReference TS typeReference
import { HttpResponse, okhttp3ResponseReadJson } from './HttpResponse.actual'
import { HttpResponseException } from './HttpResponseError.actual'
import { Observable } from 'rxjs'
import { map as rxMap } from 'rxjs/operators'

//! Declares com.lightningkite.khrysalis.net.unsuccessfulAsError
export function ioReactivexSingleUnsuccessfulAsError(this_: Observable< HttpResponse>): Observable<HttpResponse>{
    return this_.pipe(rxMap((it) => {
                if(getOkhttp3ResponseIsSuccessful(it)){
                    return it;
                } else {
                    throw new HttpResponseException(it, undefined);
                }
    }));
}


//! Declares com.lightningkite.khrysalis.net.readJson
export function ioReactivexSingleReadJson<T>(this_: Observable< HttpResponse>, T: any): Observable<T>{
    const typeReference = jacksonTypeRef<T>([null]);
    
    return this_.pipe(rxMap((it) => {
                if(getOkhttp3ResponseIsSuccessful(it)){
                    return okhttp3ResponseReadJson(it[null], , typeReference);
                } else {
                    throw new HttpResponseException(it, undefined);
                }
    }));
}

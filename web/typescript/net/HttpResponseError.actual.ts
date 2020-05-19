// Generated by Khrysalis TypeScript converter - this file will be overwritten.
// File: net/HttpResponseError.actual.kt
// Package: com.lightningkite.khrysalis.net
// FQImport: com.lightningkite.khrysalis.net.HttpResponseException.<init>.response TS response
// FQImport: kotlin.Throwable TS Throwable
// FQImport: com.lightningkite.khrysalis.net.HttpResponse TS HttpResponse
// FQImport: com.lightningkite.khrysalis.net.code TS getOkhttp3ResponseCode
// FQImport: com.lightningkite.khrysalis.net.HttpResponseException.<init>.cause TS cause
// FQImport: java.lang.Exception TS Exception
import { HttpResponse, getOkhttp3ResponseCode } from './HttpResponse.actual'

//! Declares com.lightningkite.khrysalis.net.HttpResponseException
export class HttpResponseException extends Exception {
    public readonly response: HttpResponse;
    public constructor(response: HttpResponse, cause: (Throwable | null) = null) {
        super(`Got code ${getOkhttp3ResponseCode(response)}`, cause);
        this.response = response;
    }
}

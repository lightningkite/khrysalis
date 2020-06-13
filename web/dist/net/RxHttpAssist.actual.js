"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
// Generated by Khrysalis TypeScript converter
// File: net/RxHttpAssist.actual.kt
// Package: com.lightningkite.khrysalis.net
const HttpResponseError_actual_1 = require("./HttpResponseError.actual");
const rxjs_1 = require("rxjs");
const operators_1 = require("rxjs/operators");
const jsonParsing_1 = require("./jsonParsing");
//! Declares com.lightningkite.khrysalis.net.unsuccessfulAsError
function ioReactivexSingleUnsuccessfulAsError(this_) {
    return this_.pipe(operators_1.map((it) => {
        if (it.ok) {
            return it;
        }
        else {
            throw new HttpResponseError_actual_1.HttpResponseException(it, undefined);
        }
    }));
}
exports.ioReactivexSingleUnsuccessfulAsError = ioReactivexSingleUnsuccessfulAsError;
//! Declares com.lightningkite.khrysalis.net.readJson
function ioReactivexSingleReadJson(this_, T) {
    return this_.pipe(operators_1.switchMap((it) => {
        if (it.ok) {
            return rxjs_1.from(it.json());
        }
        else {
            throw new HttpResponseError_actual_1.HttpResponseException(it, undefined);
        }
    }), operators_1.map((it) => {
        return jsonParsing_1.parse(it, T);
    }));
}
exports.ioReactivexSingleReadJson = ioReactivexSingleReadJson;
//! Declares com.lightningkite.khrysalis.net.readText
function ioReactivexSingleReadText(this_) {
    return this_.pipe(operators_1.switchMap((it) => {
        if (it.ok) {
            return rxjs_1.from(it.text());
        }
        else {
            throw new HttpResponseError_actual_1.HttpResponseException(it, undefined);
        }
    }));
}
exports.ioReactivexSingleReadText = ioReactivexSingleReadText;
//! Declares com.lightningkite.khrysalis.net.readData
function ioReactivexSingleReadData(this_) {
    return this_.pipe(operators_1.switchMap((it) => {
        if (it.ok) {
            return rxjs_1.from(it.arrayBuffer());
        }
        else {
            throw new HttpResponseError_actual_1.HttpResponseException(it, undefined);
        }
    }), operators_1.map((it) => new Int8Array(it)));
}
exports.ioReactivexSingleReadData = ioReactivexSingleReadData;
//! Declares com.lightningkite.khrysalis.net.readHttpException
function ioReactivexSingleReadHttpException(this_) {
    return this_.pipe(operators_1.catchError((err) => {
        if (err instanceof HttpResponseError_actual_1.HttpResponseException) {
            return rxjs_1.from(err.response.text())
                .pipe(operators_1.map((text) => {
                throw new HttpResponseError_actual_1.HttpReadResponseException(err.response, text, err.cause);
            }));
        }
        else {
            return rxjs_1.throwError(err);
        }
    }));
}
exports.ioReactivexSingleReadHttpException = ioReactivexSingleReadHttpException;

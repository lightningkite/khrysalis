"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
const Kotlin_1 = require("../Kotlin");
//! Declares com.lightningkite.khrysalis.net.HttpResponseException
class HttpResponseException extends Kotlin_1.Exception {
    constructor(response, cause = null) {
        super(`Got code ${response.status}`, cause);
        this.response = response;
    }
}
exports.HttpResponseException = HttpResponseException;
//! Declares com.lightningkite.khrysalis.net.HttpReadResponseException
class HttpReadResponseException extends Kotlin_1.Exception {
    constructor(response, text, cause = null) {
        super(`Got code ${response.status}`, cause);
        this.response = response;
        this.text = text;
    }
}
exports.HttpReadResponseException = HttpReadResponseException;
//# sourceMappingURL=HttpResponseError.actual.js.map
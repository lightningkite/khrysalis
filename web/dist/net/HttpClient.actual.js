"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
// Generated by Khrysalis TypeScript converter
// File: net/HttpClient.actual.kt
// Package: com.lightningkite.khrysalis.net
const rxjs_1 = require("rxjs");
const ConnectedWebSocket_actual_1 = require("./ConnectedWebSocket.actual");
//! Declares com.lightningkite.khrysalis.net.HttpClient
class HttpClient {
    constructor() {
        this.GET = "GET";
        this.POST = "POST";
        this.PUT = "PUT";
        this.PATCH = "PATCH";
        this.DELETE = "DELETE";
        //--- HttpClient.ioScheduler
        this.ioScheduler = null;
        //--- HttpClient.responseScheduler
        this.responseScheduler = null;
    }
    call(url, method = HttpClient.INSTANCE.GET, headers = new Map([]), body = null) {
        let h = new Array(...headers.entries());
        if (body !== null) {
            h.push(["Content-Type", body.type]);
        }
        return rxjs_1.from(fetch(url, {
            body: body === null || body === void 0 ? void 0 : body.data,
            cache: "no-cache",
            credentials: "omit",
            headers: h,
            method: method
        }));
    }
    webSocket(url) {
        return rxjs_1.using(() => {
            const out = new ConnectedWebSocket_actual_1.ConnectedWebSocket(url);
            // out.underlyingSocket =
            return out;
        }, (r) => r.ownConnection);
    }
}
exports.HttpClient = HttpClient;
HttpClient.INSTANCE = new HttpClient();
//# sourceMappingURL=HttpClient.actual.js.map
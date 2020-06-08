"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
//! Declares com.lightningkite.khrysalis.rx.DisposableLambda
class DisposableLambda {
    constructor(lambda) {
        this.closed = false;
        this.lambda = lambda;
    }
    unsubscribe() {
        if (this.closed) {
            return;
        }
        this.closed = true;
        this.lambda();
    }
}
exports.DisposableLambda = DisposableLambda;

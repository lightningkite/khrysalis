"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
const EventToObservableProperty_shared_1 = require("./EventToObservableProperty.shared");
//! Declares com.lightningkite.khrysalis.observables.asObservablePropertyUnboxed>io.reactivex.Observable<com.lightningkite.khrysalis.Box>
function ioReactivexObservableAsObservablePropertyUnboxed(this_, defaultValue) {
    return new EventToObservableProperty_shared_1.EventToObservableProperty(defaultValue, this_);
}
exports.ioReactivexObservableAsObservablePropertyUnboxed = ioReactivexObservableAsObservablePropertyUnboxed;

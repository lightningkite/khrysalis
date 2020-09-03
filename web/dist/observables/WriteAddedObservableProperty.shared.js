"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
const MutableObservableProperty_shared_1 = require("./MutableObservableProperty.shared");
//! Declares com.lightningkite.khrysalis.observables.WriteAddedObservableProperty
class WriteAddedObservableProperty extends MutableObservableProperty_shared_1.MutableObservableProperty {
    constructor(basedOn, onWrite) {
        super();
        this.basedOn = basedOn;
        this.onWrite = onWrite;
    }
    //! Declares com.lightningkite.khrysalis.observables.WriteAddedObservableProperty.value
    get value() { return this.basedOn.value; }
    set value(value) {
        this.onWrite(value);
    }
    //! Declares com.lightningkite.khrysalis.observables.WriteAddedObservableProperty.onChange
    get onChange() { return this.basedOn.onChange; }
    update() {
        //Do nothing
    }
}
exports.WriteAddedObservableProperty = WriteAddedObservableProperty;
//! Declares com.lightningkite.khrysalis.observables.withWrite>com.lightningkite.khrysalis.observables.ObservableProperty<kotlin.Any>
function xObservablePropertyWithWrite(this_, onWrite) {
    return new WriteAddedObservableProperty(this_, onWrite);
}
exports.xObservablePropertyWithWrite = xObservablePropertyWithWrite;
//# sourceMappingURL=WriteAddedObservableProperty.shared.js.map
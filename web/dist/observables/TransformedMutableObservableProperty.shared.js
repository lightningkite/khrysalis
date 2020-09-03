"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
const operators_1 = require("rxjs/operators");
const MutableObservableProperty_shared_1 = require("./MutableObservableProperty.shared");
//! Declares com.lightningkite.khrysalis.observables.TransformedMutableObservableProperty
class TransformedMutableObservableProperty extends MutableObservableProperty_shared_1.MutableObservableProperty {
    constructor(basedOn, read, write) {
        super();
        this.basedOn = basedOn;
        this.read = read;
        this.write = write;
    }
    update() {
        this.basedOn.update();
    }
    //! Declares com.lightningkite.khrysalis.observables.TransformedMutableObservableProperty.value
    get value() {
        return this.read(this.basedOn.value);
    }
    set value(value) {
        this.basedOn.value = this.write(value);
    }
    //! Declares com.lightningkite.khrysalis.observables.TransformedMutableObservableProperty.onChange
    get onChange() {
        const readCopy = this.read;
        return this.basedOn.onChange.pipe(operators_1.map((it) => readCopy(it)));
    }
}
exports.TransformedMutableObservableProperty = TransformedMutableObservableProperty;
//! Declares com.lightningkite.khrysalis.observables.transformed>com.lightningkite.khrysalis.observables.MutableObservableProperty<kotlin.Any>
function xMutableObservablePropertyTransformed(this_, read, write) {
    return new TransformedMutableObservableProperty(this_, read, write);
}
exports.xMutableObservablePropertyTransformed = xMutableObservablePropertyTransformed;
//! Declares com.lightningkite.khrysalis.observables.map>com.lightningkite.khrysalis.observables.MutableObservableProperty<kotlin.Any>
function xMutableObservablePropertyMap(this_, read, write) {
    return new TransformedMutableObservableProperty(this_, read, write);
}
exports.xMutableObservablePropertyMap = xMutableObservablePropertyMap;
//# sourceMappingURL=TransformedMutableObservableProperty.shared.js.map
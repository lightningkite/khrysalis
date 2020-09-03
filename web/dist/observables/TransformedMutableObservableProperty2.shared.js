"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
const operators_1 = require("rxjs/operators");
const MutableObservableProperty_shared_1 = require("./MutableObservableProperty.shared");
//! Declares com.lightningkite.khrysalis.observables.TransformedMutableObservableProperty2
class TransformedMutableObservableProperty2 extends MutableObservableProperty_shared_1.MutableObservableProperty {
    constructor(basedOn, read, write) {
        super();
        this.basedOn = basedOn;
        this.read = read;
        this.write = write;
    }
    update() {
        this.basedOn.update();
    }
    //! Declares com.lightningkite.khrysalis.observables.TransformedMutableObservableProperty2.value
    get value() {
        return this.read(this.basedOn.value);
    }
    set value(value) {
        this.basedOn.value = this.write(this.basedOn.value, value);
    }
    //! Declares com.lightningkite.khrysalis.observables.TransformedMutableObservableProperty2.onChange
    get onChange() {
        const readCopy = this.read;
        return this.basedOn.onChange.pipe(operators_1.map((it) => readCopy(it)));
    }
}
exports.TransformedMutableObservableProperty2 = TransformedMutableObservableProperty2;
//! Declares com.lightningkite.khrysalis.observables.mapWithExisting>com.lightningkite.khrysalis.observables.MutableObservableProperty<kotlin.Any>
function xMutableObservablePropertyMapWithExisting(this_, read, write) {
    return new TransformedMutableObservableProperty2(this_, read, write);
}
exports.xMutableObservablePropertyMapWithExisting = xMutableObservablePropertyMapWithExisting;
//# sourceMappingURL=TransformedMutableObservableProperty2.shared.js.map
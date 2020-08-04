"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
const MutableObservableProperty_shared_1 = require("./MutableObservableProperty.shared");
//! Declares com.lightningkite.khrysalis.observables.ReferenceObservableProperty
class ReferenceObservableProperty extends MutableObservableProperty_shared_1.MutableObservableProperty {
    constructor(get, set, event) {
        super();
        this.get = get;
        this.set = set;
        this.event = event;
    }
    //! Declares com.lightningkite.khrysalis.observables.ReferenceObservableProperty.onChange
    get onChange() { return this.event; }
    //! Declares com.lightningkite.khrysalis.observables.ReferenceObservableProperty.value
    get value() { return this.get(); }
    set value(value) {
        this.set(value);
    }
    update() {
        //do nothing
    }
}
exports.ReferenceObservableProperty = ReferenceObservableProperty;
//# sourceMappingURL=ReferenceObservableProperty.shared.js.map
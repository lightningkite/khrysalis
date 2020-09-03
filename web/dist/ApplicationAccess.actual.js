"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
const ConstantObservableProperty_shared_1 = require("./observables/ConstantObservableProperty.shared");
const WriteAddedObservableProperty_shared_1 = require("./observables/WriteAddedObservableProperty.shared");
//! Declares com.lightningkite.khrysalis.ApplicationAccess
class ApplicationAccess {
    constructor() {
        this.foreground = new ConstantObservableProperty_shared_1.ConstantObservableProperty(true);
        this.softInputActive = WriteAddedObservableProperty_shared_1.xObservablePropertyWithWrite(new ConstantObservableProperty_shared_1.ConstantObservableProperty(false), (x) => { });
    }
}
exports.ApplicationAccess = ApplicationAccess;
ApplicationAccess.INSTANCE = new ApplicationAccess();
//# sourceMappingURL=ApplicationAccess.actual.js.map
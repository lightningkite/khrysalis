"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
const ConstantObservableProperty_shared_1 = require("./observables/ConstantObservableProperty.shared");
class ApplicationAccess {
    constructor() {
        this.foreground = new ConstantObservableProperty_shared_1.ConstantObservableProperty(true);
        this.softInputActive = new ConstantObservableProperty_shared_1.ConstantObservableProperty(false);
    }
}
exports.ApplicationAccess = ApplicationAccess;
ApplicationAccess.INSTANCE = new ApplicationAccess();
//# sourceMappingURL=ApplicationAccess.actual.js.map
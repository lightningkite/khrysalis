"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
var EntryPointDefaults;
(function (EntryPointDefaults) {
    function handleDeepLink(this_, schema, host, path, params) {
        console.log(`Empty handler; ${schema}://${host}/${path}/${params}`);
    }
    EntryPointDefaults.handleDeepLink = handleDeepLink;
    function getMainStack(this_) { return null; }
    EntryPointDefaults.getMainStack = getMainStack;
})(EntryPointDefaults = exports.EntryPointDefaults || (exports.EntryPointDefaults = {}));
//# sourceMappingURL=EntryPoint.shared.js.map
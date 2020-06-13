"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
class EntryPointDefaults {
    static handleDeepLink(this_, schema, host, path, params) {
        console.log(`Empty handler; ${schema}://${host}/${path}/${params}`);
    }
    static onBackPressed(this_) { return false; }
    static getMainStack(this_) { return null; }
}
exports.EntryPointDefaults = EntryPointDefaults;

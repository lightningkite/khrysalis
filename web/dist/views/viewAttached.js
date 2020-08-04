"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
exports.detatchEventSymbol = Symbol("detatchEvent");
function addDetatchEvent(view, action) {
    let existing = view[exports.detatchEventSymbol];
    if (Array.isArray(existing)) {
        existing.push(action);
    }
    else {
        view[exports.detatchEventSymbol] = [action];
    }
}
exports.addDetatchEvent = addDetatchEvent;
function triggerDetatchEvent(view) {
    let existing = view[exports.detatchEventSymbol];
    if (Array.isArray(existing)) {
        for (const element of existing) {
            element(view);
        }
    }
    for (let i = 0; i < view.childNodes.length; i++) {
        const child = view.childNodes.item(i);
        if (child instanceof HTMLElement) {
            triggerDetatchEvent(child);
        }
    }
}
exports.triggerDetatchEvent = triggerDetatchEvent;
//# sourceMappingURL=viewAttached.js.map
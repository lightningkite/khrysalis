"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
exports.attachmentEventSymbol = Symbol("attachmentEvent");
function isAttached(view) {
    return document.body.contains(view);
}
exports.isAttached = isAttached;
function addAttachmentEvent(view, action) {
    let existing = view[exports.attachmentEventSymbol];
    if (Array.isArray(existing)) {
        existing.push(action);
    }
    else {
        view[exports.attachmentEventSymbol] = [view];
    }
}
exports.addAttachmentEvent = addAttachmentEvent;
function triggerAttachmentEvent(view) {
    let existing = view[exports.attachmentEventSymbol];
    if (Array.isArray(existing)) {
        for (const element of existing) {
            element(view);
        }
    }
}
exports.triggerAttachmentEvent = triggerAttachmentEvent;

"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
function loadHtmlFromString(data) {
    const d = document.createElement("div");
    d.innerHTML = data;
    return d.firstChild;
}
exports.loadHtmlFromString = loadHtmlFromString;
function findViewById(view, id) {
    var _a, _b;
    if (view.classList.contains(`id-${id}`))
        return (_a = view) !== null && _a !== void 0 ? _a : null;
    return (_b = view.getElementsByClassName(`id-${id}`)[0]) !== null && _b !== void 0 ? _b : null;
}
exports.findViewById = findViewById;
function getViewById(view, id) {
    if (view.classList.contains(`id-${id}`))
        return view;
    return view.getElementsByClassName(`id-${id}`)[0];
}
exports.getViewById = getViewById;
function replaceViewWithId(base, withElement, id) {
    const existing = findViewById(base, id);
    if (existing) {
        const newElement = withElement();
        newElement.setAttribute('style', newElement.getAttribute('style') + '; ' + existing.getAttribute('style'));
        existing.parentNode.replaceChild(newElement, existing);
    }
}
exports.replaceViewWithId = replaceViewWithId;
//# sourceMappingURL=html.js.map
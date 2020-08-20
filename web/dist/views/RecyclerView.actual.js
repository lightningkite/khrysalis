"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
const Language_1 = require("../kotlin/Language");
function scrollChildIntoView(e, index, options) {
    var _a;
    (_a = Language_1.tryCastClass(e.children.item(index), HTMLElement)) === null || _a === void 0 ? void 0 : _a.scrollIntoView(options);
}
exports.scrollChildIntoView = scrollChildIntoView;
//# sourceMappingURL=RecyclerView.actual.js.map
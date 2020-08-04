"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
const ViewDependency_actual_1 = require("./views/ViewDependency.actual");
function main(rootVg) {
    const view = rootVg.generate(window);
    document.body.appendChild(view);
    ViewDependency_actual_1.listenForDialogs();
}
exports.main = main;
//# sourceMappingURL=main.js.map
"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
const ViewDependency_actual_1 = require("./views/ViewDependency.actual");
const HttpClient_actual_1 = require("./net/HttpClient.actual");
const rxjs_1 = require("rxjs");
function main(rootVg) {
    HttpClient_actual_1.HttpClient.INSTANCE.ioScheduler = rxjs_1.asyncScheduler;
    HttpClient_actual_1.HttpClient.INSTANCE.responseScheduler = rxjs_1.asyncScheduler;
    const view = rootVg.generate(window);
    document.body.appendChild(view);
    ViewDependency_actual_1.listenForDialogs();
}
exports.main = main;
//# sourceMappingURL=main.js.map
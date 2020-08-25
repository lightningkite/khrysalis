"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
const Language_1 = require("../kotlin/Language");
//! Declares com.lightningkite.khrysalis.views.DrawableResource
class DrawableResource {
    constructor(cssClass, filePath) {
        this.cssClass = cssClass;
        this.filePath = filePath;
    }
    hashCode() {
        let hash = 17;
        hash = 31 * hash + Language_1.hashAnything(this.cssClass);
        return hash;
    }
    equals(other) { return other instanceof DrawableResource && Language_1.safeEq(this.cssClass, other.cssClass); }
    toString() { return `DrawableResource(cssClass = ${this.cssClass})`; }
}
exports.DrawableResource = DrawableResource;
//# sourceMappingURL=DrawableResource.js.map
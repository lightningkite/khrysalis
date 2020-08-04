"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
const Kotlin_1 = require("../../Kotlin");
//! Declares com.lightningkite.khrysalis.views.geometry.PolygonF
class PolygonF {
    constructor(points) {
        this.points = points;
    }
    hashCode() {
        var _a, _b;
        let hash = 17;
        hash = (_b = 31 * hash + ((_a = this.points) === null || _a === void 0 ? void 0 : _a.hashCode())) !== null && _b !== void 0 ? _b : 0;
        return hash;
    }
    equals(other) { return other instanceof PolygonF && Kotlin_1.safeEq(this.points, other.points); }
    toString() { return `PolygonF(points = ${this.points})`; }
    copy(points = this.points) { return new PolygonF(points); }
    contains(point) {
        let inside = false;
        const big = 1000;
        for (const index of (new Kotlin_1.NumberRange(0, this.points.length - 2 - 1))) {
            const a = this.points[index];
            const b = this.points[index + 1];
            const denom = (-(big - point.x)) * (b.y - a.y);
            if (denom === 0) {
                continue;
            }
            const ua = ((big - point.x) * (a.y - point.y)) / denom;
            const ub = ((b.x - a.x) * (a.y - point.y) - (b.y - a.y) * (a.x - point.x)) / denom;
            if (ua >= 0.0 && ua <= 1.0 && ub >= 0.0 && ub <= 1.0) {
                inside = (!inside);
            }
        }
        return inside;
    }
}
exports.PolygonF = PolygonF;
//# sourceMappingURL=PolygonF.shared.js.map
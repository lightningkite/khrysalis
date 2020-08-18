"use strict";
// Generated by Khrysalis TypeScript converter - this file will be overwritten.
// File: views/geometry/Geometry.shared.kt
// Package: com.lightningkite.khrysalis.views.geometry
Object.defineProperty(exports, "__esModule", { value: true });
//! Declares com.lightningkite.khrysalis.views.geometry.Geometry
class Geometry {
    constructor() {
    }
    rayIntersectsLine(rayX, rayY, rayToX, rayToY, lineX1, lineY1, lineX2, lineY2) {
        const denom = (rayToY - rayY) * (lineX2 - lineX1) - (rayToX - rayX) * (lineY2 - lineY1);
        if (denom === 0) {
            return false;
        }
        const lineRatio = ((rayToX - rayX) * (lineY1 - rayY) - (rayToY - rayY) * (lineX1 - rayX)) / denom;
        const rayRatio = ((lineX2 - lineX1) * (lineY1 - rayY) - (lineY2 - lineY1) * (lineX1 - rayX)) / denom;
        return lineRatio >= 0.0 && lineRatio <= 1.0 && rayRatio > 0;
    }
}
exports.Geometry = Geometry;
Geometry.INSTANCE = new Geometry();
//# sourceMappingURL=Geometry.shared.js.map
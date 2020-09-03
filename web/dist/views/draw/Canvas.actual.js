"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
const Align_shared_1 = require("../geometry/Align.shared");
//! Declares com.lightningkite.khrysalis.views.draw.drawTextCentered>android.graphics.Canvas
function xCanvasDrawTextCentered(this_, text, centerX, centerY, paint) {
    this_.textBaseline = "middle";
    this_.textAlign = "center";
    paint.text(this_, text, centerX, centerY);
}
exports.xCanvasDrawTextCentered = xCanvasDrawTextCentered;
//! Declares com.lightningkite.khrysalis.views.draw.drawText>android.graphics.Canvas
function xCanvasDrawText(this_, text, x, y, gravity, paint) {
    switch (gravity.vertical) {
        case Align_shared_1.Align.start:
            this_.textBaseline = "top";
            break;
        case Align_shared_1.Align.center:
        case Align_shared_1.Align.fill:
            this_.textBaseline = "middle";
            break;
        case Align_shared_1.Align.end:
            this_.textBaseline = "bottom";
            break;
    }
    this_.textAlign = "center";
    paint.text(this_, text, x, y);
}
exports.xCanvasDrawText = xCanvasDrawText;
//! Declares com.lightningkite.khrysalis.views.draw.drawBitmap
function xCanvasDrawBitmap(this_, bitmap, left, top, right, bottom) {
    this_.drawImage(bitmap, left, top, right - left, bottom - top);
}
exports.xCanvasDrawBitmap = xCanvasDrawBitmap;
function applyMatrixToCanvas(canvas, matrix) {
    canvas.transform(matrix.a, matrix.b, matrix.c, matrix.d, matrix.e, matrix.f);
}
exports.applyMatrixToCanvas = applyMatrixToCanvas;
/*

CUSTOM DRAWING

- Custom view w/ touch interaction
- Calendar view custom callback - perhaps override on both sides, and it'd be OK?

MonthView - Can take custom renderer and touch handler
CalendarView - Can take MonthView generator, show specific months

*/
//# sourceMappingURL=Canvas.actual.js.map
"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
const CustomView_actual_1 = require("./CustomView.actual");
//! Declares com.lightningkite.khrysalis.views.CustomViewDelegate
class CustomViewDelegate {
    onTouchDown(id, x, y, width, height) { return false; }
    onTouchMove(id, x, y, width, height) { return false; }
    onTouchCancelled(id, x, y, width, height) { return false; }
    onTouchUp(id, x, y, width, height) { return false; }
    sizeThatFitsWidth(width, height) { return width; }
    sizeThatFitsHeight(width, height) { return height; }
    invalidate() {
        const temp133 = this.customView;
        if (temp133 !== null)
            CustomView_actual_1.customViewInvalidate(temp133);
    }
    postInvalidate() {
        const temp134 = this.customView;
        if (temp134 !== null)
            CustomView_actual_1.customViewInvalidate(temp134);
    }
}
exports.CustomViewDelegate = CustomViewDelegate;

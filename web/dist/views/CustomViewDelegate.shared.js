"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
const DisposeCondition_shared_1 = require("../rx/DisposeCondition.shared");
const CustomView_actual_1 = require("./CustomView.actual");
//! Declares com.lightningkite.khrysalis.views.CustomViewDelegate
class CustomViewDelegate {
    constructor() {
        this.customView = null;
        this.toDispose = [];
        this._removed = null;
        this._removed = new DisposeCondition_shared_1.DisposeCondition((it) => {
            this.toDispose.push(it);
        });
    }
    onTouchDown(id, x, y, width, height) { return false; }
    onTouchMove(id, x, y, width, height) { return false; }
    onTouchCancelled(id, x, y, width, height) { return false; }
    onTouchUp(id, x, y, width, height) { return false; }
    sizeThatFitsWidth(width, height) { return width; }
    sizeThatFitsHeight(width, height) { return height; }
    invalidate() {
        const temp190 = this.customView;
        if (temp190 !== null) {
            CustomView_actual_1.customViewInvalidate(temp190);
        }
        ;
    }
    postInvalidate() {
        const temp191 = this.customView;
        if (temp191 !== null) {
            CustomView_actual_1.customViewInvalidate(temp191);
        }
        ;
    }
    //! Declares com.lightningkite.khrysalis.views.CustomViewDelegate.removed
    get removed() { return this._removed; }
    dispose() {
        for (const item of this.toDispose) {
            item.unsubscribe();
        }
    }
}
exports.CustomViewDelegate = CustomViewDelegate;
//# sourceMappingURL=CustomViewDelegate.shared.js.map
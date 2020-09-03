"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
//! Declares com.lightningkite.khrysalis.views.setOnDoneClick
function xEditTextSetOnDoneClick(editText, action) {
    editText.addEventListener("keyup", function (event) {
        if (event.key === "Enter") {
            action();
        }
    });
}
exports.xEditTextSetOnDoneClick = xEditTextSetOnDoneClick;
//# sourceMappingURL=EditText.js.map
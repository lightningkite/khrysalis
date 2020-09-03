"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
//! Declares com.lightningkite.khrysalis.net.WebSocketFrame
class WebSocketFrame {
    constructor(binary = null, text = null) {
        this.binary = binary;
        this.text = text;
    }
    toString() {
        var _a, _b;
        return (_b = (_a = this.text) !== null && _a !== void 0 ? _a : (() => {
            const it_20 = this.binary;
            if (it_20 !== null) {
                return `<Binary data length ${it_20.length}`;
            }
            else {
                return null;
            }
        })()) !== null && _b !== void 0 ? _b : "<Empty Frame>";
    }
}
exports.WebSocketFrame = WebSocketFrame;
//# sourceMappingURL=WebSocketFrame.actual.js.map
"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
const viewAttached_1 = require("./viewAttached");
const previousViewSymbol = Symbol("previousView");
function swapViewSwap(view, to, animation) {
    if (to) {
        to.style.width = "100%";
        to.style.height = "100%";
    }
    const current = view[previousViewSymbol];
    if (to === current) {
        if (!to) {
            view.hidden = true;
            view.innerHTML = "";
        }
        return;
    }
    if (current) {
        //animate out
        const animationOut = `${animation}-out`;
        let animOutHandler;
        animOutHandler = (ev) => {
            current.removeEventListener("animationend", animOutHandler);
            view.removeChild(current);
            viewAttached_1.triggerDetatchEvent(current);
        };
        current.addEventListener("animationend", animOutHandler);
        current.style.animation = `${animationOut} 0.25s`;
        //animate in
        if (to) {
            view.hidden = false;
            const animationIn = `${animation}-in`;
            let animInHandler;
            animInHandler = (ev) => {
                to.onanimationend = null;
                to.style.removeProperty("animation");
            };
            to.addEventListener("animationend", animInHandler);
            to.style.animation = `${animationIn} 0.25s 0.25s`; //Delay seems to make this work right
            view.appendChild(to);
        }
        else {
            view.hidden = true;
            view.innerHTML = "";
        }
    }
    else if (to) {
        view.appendChild(to);
        view.hidden = false;
    }
    else {
        view.hidden = true;
        view.innerHTML = "";
    }
    view[previousViewSymbol] = to !== null && to !== void 0 ? to : undefined;
}
exports.swapViewSwap = swapViewSwap;
//# sourceMappingURL=SwapView.js.map
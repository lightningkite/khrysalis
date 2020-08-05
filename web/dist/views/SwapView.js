"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
const viewAttached_1 = require("./viewAttached");
const previousViewSymbol = Symbol("previousView");
function swapViewSwap(view, to, animation) {
    if (to) {
        to.style.width = "100%";
        to.style.height = "100%";
    }
    view.style.visibility = "visible";
    const current = view[previousViewSymbol];
    if (current) {
        //animate out
        const animationOut = `${animation}-out`;
        let animOutHandler;
        animOutHandler = (ev) => {
            if (ev.animationName === animationOut) {
                current.removeEventListener("animationend", animOutHandler);
                view.removeChild(current);
                viewAttached_1.triggerDetatchEvent(current);
            }
        };
        current.addEventListener("animationend", animOutHandler);
        current.style.animation = `${animationOut} 0.25s`;
        //animate in
        if (to) {
            const animationIn = `${animation}-in`;
            let animInHandler;
            animInHandler = (ev) => {
                if (ev.animationName === animationIn) {
                    to.onanimationend = null;
                    to.style.removeProperty("animation");
                }
            };
            to.addEventListener("animationend", animInHandler);
            to.style.animation = `${animationIn} 0.25s 0.25s`; //Delay seems to make this work right
            view.appendChild(to);
        }
        else {
            view.style.visibility = "hidden"; //TODO: Smooth fade would look nicer
            console.log("Hiding myself... I'm shy");
        }
    }
    else if (to) {
        view.appendChild(to);
    }
    else {
        view.style.visibility = "hidden";
        console.log("Hiding myself... I'm shy");
    }
    view[previousViewSymbol] = to;
}
exports.swapViewSwap = swapViewSwap;
//# sourceMappingURL=SwapView.js.map
"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
const DisposeCondition_actual_1 = require("../../rx/DisposeCondition.actual");
//! Declares com.lightningkite.khrysalis.observables.binding.bindLoading>android.widget.ViewFlipper
function xViewFlipperBindLoading(this_, loading, color = null) {
    var _a;
    const mainChild = this_.firstElementChild;
    const loadingChild = (_a = this_.children.item(1)) !== null && _a !== void 0 ? _a : (() => {
        const newElement = document.createElement("div");
        newElement.classList.add("khrysalis-flipper-progress");
        newElement.classList.add("khr");
        this_.appendChild(newElement);
        return newElement;
    })();
    const animation = "khrysalis-animate-fade";
    let currentView = mainChild;
    let hiddenView = loadingChild;
    DisposeCondition_actual_1.xDisposableUntil(loading.onChange.subscribe((e) => {
        if (e) {
            hiddenView = mainChild;
            currentView = loadingChild;
        }
        else {
            currentView = mainChild;
            hiddenView = loadingChild;
        }
        // currentView.style.removeProperty("animation");
        // hiddenView.style.removeProperty("animation");
        // currentView.style.removeProperty("visibility");
        // hiddenView.style.removeProperty("visibility");
        const viewOut = hiddenView;
        const viewIn = currentView;
        viewOut.style.setProperty("visibility", "hidden", "important");
        viewIn.style.removeProperty("visibility");
        // //animate out
        // const animationOut = `${animation}-out`;
        // let animOutHandler: (ev: AnimationEvent) => void;
        // animOutHandler = (ev: AnimationEvent) => {
        //     if (ev.animationName === animationOut) {
        //         viewOut.onanimationend = null;
        //         viewOut.style.visibility = "hidden";
        //     }
        // };
        // viewOut.onanimationend = animOutHandler;
        // viewOut.style.animation = `${animationOut} 0.25s`;
        //
        // //animate in
        // const animationIn = `${animation}-in`;
        // let animInHandler: (ev: AnimationEvent) => void;
        // animInHandler = (ev: AnimationEvent) => {
        //     if (ev.animationName === animationIn) {
        //         viewIn.onanimationend = null;
        //         viewIn.style.removeProperty("animation");
        //     }
        // };
        // viewIn.onanimationend = animInHandler;
        // viewIn.style.animation = `${animationIn} 0.25s`;
    }), DisposeCondition_actual_1.xViewRemovedGet(this_));
    if (loading.value) {
        hiddenView = mainChild;
        currentView = loadingChild;
    }
    else {
        currentView = mainChild;
        hiddenView = loadingChild;
    }
    hiddenView.style.setProperty("visibility", "hidden", "important");
}
exports.xViewFlipperBindLoading = xViewFlipperBindLoading;
//# sourceMappingURL=ViewFlipper.binding.actual.js.map
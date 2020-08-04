"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
const DisposeCondition_actual_1 = require("../../rx/DisposeCondition.actual");
//! Declares com.lightningkite.khrysalis.observables.binding.bindLoading>android.widget.ViewFlipper
function androidWidgetViewFlipperBindLoading(this_, loading, color = null) {
    var _a;
    const mainChild = this_.firstElementChild;
    const loadingChild = (_a = this_.children.item(1)) !== null && _a !== void 0 ? _a : (() => {
        const newElement = document.createElement("progress");
        newElement.classList.add("khrysalis-flipper-progress");
        this_.appendChild(newElement);
        return newElement;
    })();
    const animation = "khrysalis-animate-fade";
    let currentView = mainChild;
    let hiddenView = loadingChild;
    DisposeCondition_actual_1.ioReactivexDisposablesDisposableUntil(loading.onChange.subscribe((e) => {
        if (e) {
            hiddenView = mainChild;
            currentView = loadingChild;
        }
        else {
            currentView = mainChild;
            hiddenView = loadingChild;
        }
        currentView.style.animation = "none";
        hiddenView.style.animation = "none";
        //animate out
        const animationOut = `${animation}-out`;
        let animOutHandler;
        animOutHandler = (ev) => {
            if (ev.animationName === animationOut) {
                ev.target.onanimationend = null;
                ev.target.style.visibility = "hidden";
            }
        };
        hiddenView.onanimationend = animOutHandler;
        hiddenView.style.animation = `${animationOut} 0.25s`;
        //animate in
        const animationIn = `${animation}-in`;
        currentView.style.visibility = "visible";
        currentView.style.animation = `${animationIn} 0.25s`;
    }), DisposeCondition_actual_1.getAndroidViewViewRemoved(this_));
    if (loading.value) {
        hiddenView = mainChild;
        currentView = loadingChild;
    }
    else {
        currentView = mainChild;
        hiddenView = loadingChild;
    }
    currentView.style.visibility = "visible";
    hiddenView.style.visibility = "hidden";
}
exports.androidWidgetViewFlipperBindLoading = androidWidgetViewFlipperBindLoading;
//# sourceMappingURL=ViewFlipper.binding.actual.js.map
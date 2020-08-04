"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
const DisposeCondition_actual_1 = require("../../rx/DisposeCondition.actual");
const ObservableProperty_ext_shared_1 = require("../ObservableProperty.ext.shared");
//! Declares com.lightningkite.khrysalis.observables.binding.bind>android.widget.AutoCompleteTextView
function androidWidgetAutoCompleteTextViewBind(this_, options, toString, onItemSelected) {
    const container = this_.parentElement;
    let selectionView = null;
    function removeOptions() {
        console.log("Removing");
        if (selectionView) {
            container.removeChild(selectionView);
            selectionView = null;
        }
    }
    let lastCancel = Date.now();
    function removeOptionsCancel() {
        console.log("Cancel removal");
        lastCancel = Date.now();
    }
    function removeOptionsTenative() {
        console.log("Tenative removal set");
        window.setTimeout(() => {
            console.log(`Remove? Diff is ${Date.now() - lastCancel}`);
            if (Date.now() - lastCancel > 150) {
                removeOptions();
            }
        }, 100);
    }
    function showOptions(query, options) {
        removeOptions();
        console.log("Showing Options");
        const newSelectionView = document.createElement("div");
        newSelectionView.tabIndex = -1;
        newSelectionView.classList.add("khrysalis-autocomplete-options");
        const matchingOptions = options.filter((x) => toString(x).toLowerCase().indexOf(query.toLowerCase()) != -1);
        for (const option of matchingOptions) {
            const optionView = document.createElement("button");
            optionView.tabIndex = 0;
            optionView.classList.add("khrysalis-autocomplete-option");
            optionView.innerText = toString(option);
            optionView.addEventListener("click", (ev) => {
                ev.stopPropagation();
                onItemSelected(option);
                removeOptions();
            });
            optionView.addEventListener("blur", (ev) => {
                removeOptionsTenative();
            });
            optionView.addEventListener("focus", (ev) => {
                removeOptionsCancel();
            });
            optionView.addEventListener("keydown", (ev) => {
                switch (ev.code) {
                    case "ArrowDown":
                        ev.preventDefault();
                        let child = optionView.nextElementSibling;
                        if (child) {
                            child.focus();
                        }
                        break;
                    case "ArrowUp":
                        ev.preventDefault();
                        let child2 = optionView.previousElementSibling;
                        if (child2) {
                            child2.focus();
                        }
                        else {
                            this_.focus();
                        }
                        break;
                }
            });
            newSelectionView.appendChild(optionView);
        }
        container.appendChild(newSelectionView);
        selectionView = newSelectionView;
    }
    this_.addEventListener("blur", (ev) => {
        removeOptionsTenative();
    });
    this_.addEventListener("focus", (ev) => {
        removeOptionsCancel();
    });
    this_.addEventListener("input", (ev) => {
        showOptions(this_.value, options.value);
    });
    this_.addEventListener("keydown", (ev) => {
        switch (ev.code) {
            case "ArrowDown":
                ev.preventDefault();
                let child = selectionView === null || selectionView === void 0 ? void 0 : selectionView.firstElementChild;
                if (child) {
                    child.focus();
                }
                break;
        }
    });
    DisposeCondition_actual_1.ioReactivexDisposablesDisposableUntil(ObservableProperty_ext_shared_1.comLightningkiteKhrysalisObservablesObservablePropertySubscribeBy(options, undefined, undefined, (x) => {
        if (this_ === document.activeElement) {
            showOptions(this_.value, x);
        }
    }), DisposeCondition_actual_1.getAndroidViewViewRemoved(this_));
}
exports.androidWidgetAutoCompleteTextViewBind = androidWidgetAutoCompleteTextViewBind;
//# sourceMappingURL=AutoCompleteTextView.binding.actual.js.map
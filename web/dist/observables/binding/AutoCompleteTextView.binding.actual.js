"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
const DisposeCondition_actual_1 = require("../../rx/DisposeCondition.actual");
const ObservableProperty_ext_shared_1 = require("../ObservableProperty.ext.shared");
const CombineObservableProperty_shared_1 = require("../CombineObservableProperty.shared");
const StandardObservableProperty_shared_1 = require("../StandardObservableProperty.shared");
//! Declares com.lightningkite.khrysalis.observables.binding.bind>android.widget.AutoCompleteTextView
function xAutoCompleteTextViewBind(this_, options, toString, onItemSelected) {
    let query = new StandardObservableProperty_shared_1.StandardObservableProperty("");
    this_.addEventListener("change", () => {
        query.value = this_.value;
    });
    xAutoCompleteTextViewBindList(this_, CombineObservableProperty_shared_1.xObservablePropertyCombine(options, query, (options, query) => options.filter((x) => toString(x).toLowerCase().indexOf(query.toLowerCase()) != -1)), toString, onItemSelected);
}
exports.xAutoCompleteTextViewBind = xAutoCompleteTextViewBind;
//! Declares com.lightningkite.khrysalis.observables.binding.bindList>android.widget.AutoCompleteTextView
function xAutoCompleteTextViewBindList(this_, options, toString, onItemSelected) {
    const container = this_.parentElement;
    let selectionView = null;
    function removeOptions() {
        if (selectionView) {
            container.removeChild(selectionView);
            selectionView = null;
        }
    }
    let lastCancel = Date.now();
    function removeOptionsCancel() {
        lastCancel = Date.now();
    }
    function removeOptionsTenative() {
        window.setTimeout(() => {
            if (Date.now() - lastCancel > 150) {
                removeOptions();
            }
        }, 100);
    }
    function showOptions(query, options) {
        removeOptions();
        const newSelectionView = document.createElement("div");
        newSelectionView.tabIndex = -1;
        newSelectionView.classList.add("khrysalis-autocomplete-options");
        for (const option of options) {
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
    DisposeCondition_actual_1.xDisposableUntil(ObservableProperty_ext_shared_1.xObservablePropertySubscribeBy(options, undefined, undefined, (x) => {
        if (this_ === document.activeElement) {
            showOptions(this_.value, x);
        }
    }), DisposeCondition_actual_1.xViewRemovedGet(this_));
}
exports.xAutoCompleteTextViewBindList = xAutoCompleteTextViewBindList;
//# sourceMappingURL=AutoCompleteTextView.binding.actual.js.map
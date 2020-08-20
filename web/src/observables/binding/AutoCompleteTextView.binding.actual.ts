// Generated by Khrysalis TypeScript converter
// File: observables/binding/AutoCompleteTextView.binding.actual.kt
// Package: com.lightningkite.khrysalis.observables.binding
import {ObservableProperty} from '../ObservableProperty.shared'
import {getAndroidViewViewRemoved, ioReactivexDisposablesDisposableUntil} from "../../rx/DisposeCondition.actual";
import {comLightningkiteKhrysalisObservablesObservablePropertySubscribeBy} from "../ObservableProperty.ext.shared";
import {comLightningkiteKhrysalisObservablesObservablePropertyMap} from "../TransformedObservableProperty.shared";
import {comLightningkiteKhrysalisObservablesObservablePropertyCombine} from "../CombineObservableProperty.shared";
import {StandardObservableProperty} from "../StandardObservableProperty.shared";

//! Declares com.lightningkite.khrysalis.observables.binding.bind>android.widget.AutoCompleteTextView
export function androidWidgetAutoCompleteTextViewBind<T>(this_: HTMLInputElement, options: ObservableProperty<Array<T>>, toString: (a: T) => string, onItemSelected: (a: T) => void): void {
    let query = new StandardObservableProperty("")
    this_.addEventListener("change", ()=>{
        query.value = this_.value;
    });
    androidWidgetAutoCompleteTextViewBindList(
        this_,
        comLightningkiteKhrysalisObservablesObservablePropertyCombine(
            options,
            query,
            (options, query) => options.filter((x) => toString(x).toLowerCase().indexOf(query.toLowerCase()) != -1)
        ),
        toString,
        onItemSelected
    );
}
//! Declares com.lightningkite.khrysalis.observables.binding.bindList>android.widget.AutoCompleteTextView
export function androidWidgetAutoCompleteTextViewBindList<T>(this_: HTMLInputElement, options: ObservableProperty<Array<T>>, toString: (a: T) => string, onItemSelected: (a: T) => void): void {
    const container = this_.parentElement as HTMLElement;
    let selectionView: HTMLDivElement | null = null;

    function removeOptions(){
        if (selectionView) {
            container.removeChild(selectionView);
            selectionView = null;
        }
    }
    let lastCancel = Date.now();
    function removeOptionsCancel(){
        lastCancel = Date.now();
    }
    function removeOptionsTenative(){
        window.setTimeout(()=>{
            if(Date.now() - lastCancel > 150){
                removeOptions();
            }
        }, 100)
    }
    function showOptions(query: string, options: Array<T>) {
        removeOptions();
        const newSelectionView = document.createElement("div");
        newSelectionView.tabIndex = -1;
        newSelectionView.classList.add("khrysalis-autocomplete-options")
        for(const option of options) {
            const optionView = document.createElement("button");
            optionView.tabIndex = 0;
            optionView.classList.add("khrysalis-autocomplete-option")
            optionView.innerText = toString(option);
            optionView.addEventListener("click", (ev)=> {
                ev.stopPropagation();
                onItemSelected(option)
                removeOptions()
            });
            optionView.addEventListener("blur", (ev) => {
                removeOptionsTenative();
            })
            optionView.addEventListener("focus", (ev) => {
                removeOptionsCancel();
            })
            optionView.addEventListener("keydown", (ev) => {
                switch(ev.code){
                    case "ArrowDown":
                        ev.preventDefault();
                        let child = optionView.nextElementSibling as HTMLElement;
                        if(child){
                            child.focus();
                        }
                        break;
                    case "ArrowUp":
                        ev.preventDefault();
                        let child2 = optionView.previousElementSibling as HTMLElement;
                        if(child2){
                            child2.focus();
                        } else {
                            this_.focus();
                        }
                        break;
                }
            })
            newSelectionView.appendChild(optionView);
        }
        container.appendChild(newSelectionView);
        selectionView = newSelectionView;
    }
    this_.addEventListener("blur", (ev) => {
        removeOptionsTenative();
    })
    this_.addEventListener("focus", (ev) => {
        removeOptionsCancel();
    })
    this_.addEventListener("input", (ev) => {
        showOptions(this_.value, options.value);
    })
    this_.addEventListener("keydown", (ev) => {
        switch(ev.code){
        case "ArrowDown":
            ev.preventDefault();
            let child = selectionView?.firstElementChild as HTMLElement;
            if(child){
                child.focus();
            }
            break;
        }
    })
    ioReactivexDisposablesDisposableUntil(comLightningkiteKhrysalisObservablesObservablePropertySubscribeBy(options, undefined, undefined, (x)=>{
        if(this_ === document.activeElement){
            showOptions(this_.value, x);
        }
    }), getAndroidViewViewRemoved(this_))
}
// Generated by Khrysalis TypeScript converter
// File: views/CustomView.actual.kt
// Package: com.lightningkite.khrysalis.views

import {CustomViewDelegate} from "./CustomViewDelegate.shared";
import {DisplayMetrics} from "./DisplayMetrics.actual";
import {DisposableLambda, getAndroidViewViewRemoved} from "../rx/DisposeCondition.actual";
import {tryCastClass} from "../Kotlin";

const customViewDelegateSymbol = Symbol("customViewDelegateSymbol");
const customViewConfiguredSymbol = Symbol("customViewConfiguredSymbol");

declare global {
    interface HTMLCanvasElement {
        [customViewDelegateSymbol]: CustomViewDelegate | undefined
        [customViewConfiguredSymbol]: boolean | undefined
    }
}

export function customViewSetDelegate(view: HTMLCanvasElement, delegate: CustomViewDelegate) {
    view[customViewDelegateSymbol]?.dispose();
    delegate.customView = view;

    view.style.touchAction = "none";
    view.onpointerdown = (e) => {
        const b = view.getBoundingClientRect();
        delegate.onTouchDown(e.pointerId, e.pageX - b.x, e.pageY - b.y, view.width, view.height);
    }
    view.onpointermove = (e) => {
        if(e.buttons > 0){
            const b = view.getBoundingClientRect();
            delegate.onTouchMove(e.pointerId, e.pageX - b.x, e.pageY - b.y, view.width, view.height);
        }
    }
    view.onpointercancel = (e) => {
        const b = view.getBoundingClientRect();
        delegate.onTouchCancelled(e.pointerId, e.pageX - b.x, e.pageY - b.y, view.width, view.height);
    }
    view.onpointerup = (e) => {
        const b = view.getBoundingClientRect();
        delegate.onTouchUp(e.pointerId, e.pageX - b.x, e.pageY - b.y, view.width, view.height);
    }

    if(view.getContext){
        const ctx = view.getContext("2d");
        view.width = view.offsetWidth;
        view.height = view.offsetHeight;
        if(view.width > 2 && view.height > 2){
            delegate.draw(ctx, view.width, view.height, DisplayMetrics.INSTANCE);
        }
    } else {
        view.parentElement?.appendChild(delegate.generateAccessibilityView());
    }
    view[customViewDelegateSymbol] = delegate;

    if(!view[customViewConfiguredSymbol]){
        view[customViewConfiguredSymbol] = true;
        getAndroidViewViewRemoved(view).call(new DisposableLambda(()=>{
            view[customViewDelegateSymbol]?.dispose();
            view[customViewDelegateSymbol] = null;
        }))

        const p = view.parentElement;
        const adjWidth = !view.style.width && !(p.style.flexDirection == "column" && view.style.alignSelf == "stretch");
        const adjHeight = !view.style.height && !(p.style.flexDirection == "row" && view.style.alignSelf == "stretch");
        if(p){
            const obs = new ResizeObserver(function callback(){
                if(adjWidth){
                    view.style.width = delegate.sizeThatFitsWidth(p.scrollWidth, p.scrollHeight).toString() + "px";
                }
                if(adjHeight) {
                    view.style.height = delegate.sizeThatFitsHeight(p.scrollWidth, p.scrollHeight).toString() + "px";
                }
                customViewInvalidate(view);
                if(!document.contains(view)) {
                    obs.disconnect();
                }
            });
            obs.observe(p);
        }
    }
}

export function customViewInvalidate(view: HTMLCanvasElement) {
    const delegate = view[customViewDelegateSymbol];
    if(!delegate) return;
    if(view.getContext){
        const ctx = view.getContext("2d");
        view.width = view.offsetWidth;
        view.height = view.offsetHeight;
        if(view.width > 2 && view.height > 2){
            ctx.clearRect(0, 0, view.width, view.height);
            delegate.draw(ctx, view.width, view.height, DisplayMetrics.INSTANCE);
        }
    }
}
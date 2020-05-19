// Generated by Khrysalis TypeScript converter - this file will be overwritten.
// File: observables/binding/SwapView.binding.actual.kt
// Package: com.lightningkite.khrysalis.observables.binding
// FQImport: android.view.View.animate TS animate
// FQImport: com.lightningkite.khrysalis.observables.ObservableStack.stack TS stack
// FQImport: com.lightningkite.khrysalis.observables.binding.bindStack.<anonymous>.<anonymous>.newStackSize TS newStackSize
// FQImport: com.lightningkite.khrysalis.views.android.SwapView.post TS post
// FQImport: android.view.View.GONE TS GONE
// FQImport: com.lightningkite.khrysalis.views.ViewGenerator TS ViewGenerator
// FQImport: com.lightningkite.khrysalis.observables.binding.bindStack.currentView TS currentView
// FQImport: com.lightningkite.khrysalis.observables.subscribeBy TS comLightningkiteKhrysalisObservablesObservablePropertySubscribeBy
// FQImport: android.view.ViewGroup TS ViewGroup
// FQImport: com.lightningkite.khrysalis.observables.binding.bindStack.<anonymous>.<anonymous>.oldStackSize TS oldStackSize
// FQImport: com.lightningkite.khrysalis.observables.binding.bindStack.obs TS obs
// FQImport: android.view.ViewGroup.LayoutParams.MATCH_PARENT TS MATCH_PARENT
// FQImport: com.lightningkite.khrysalis.observables.binding.bindStack.<anonymous>.<anonymous>.oldView TS oldView
// FQImport: com.lightningkite.khrysalis.views.android.SwapView.removeView TS removeView
// FQImport: android.view.View TS View
// FQImport: alpha TS setAndroidViewViewAlpha
// FQImport: com.lightningkite.khrysalis.rx.until TS ioReactivexDisposablesDisposableUntil
// FQImport: com.lightningkite.khrysalis.views.android.SwapView.addView TS addView
// FQImport: com.lightningkite.khrysalis.views.ViewDependency TS ViewDependency
// FQImport: android.view.ViewPropertyAnimator.alpha TS alpha
// FQImport: com.lightningkite.khrysalis.observables.binding.bindStack.<anonymous>.<anonymous>.newView TS newView
// FQImport: com.lightningkite.khrysalis.observables.ObservableStack TS ObservableStack
// FQImport: android.view.ViewGroup.LayoutParams TS LayoutParams
// FQImport: android.view.ViewPropertyAnimator.translationX TS translationX
// FQImport: android.view.View.VISIBLE TS VISIBLE
// FQImport: com.lightningkite.khrysalis.observables.binding.bindStack.currentData TS currentData
// FQImport: visibility TS setComLightningkiteKhrysalisViewsAndroidSwapViewVisibility
// FQImport: com.lightningkite.khrysalis.observables.binding.bindStack.<anonymous>.datas TS datas
// FQImport: com.lightningkite.khrysalis.observables.binding.bindStack.currentStackSize TS currentStackSize
// FQImport: com.lightningkite.khrysalis.observables.binding.bindStack.dependency TS dependency
// FQImport: android.view.ViewPropertyAnimator.withEndAction TS withEndAction
// FQImport: translationX TS setAndroidViewViewTranslationX
// FQImport: android.widget.FrameLayout TS FrameLayout
// FQImport: com.lightningkite.khrysalis.rx.removed TS getAndroidViewViewRemoved
// FQImport: context TS getComLightningkiteKhrysalisViewsAndroidSwapViewContext
// FQImport: width TS getComLightningkiteKhrysalisViewsAndroidSwapViewWidth
// FQImport: com.lightningkite.khrysalis.views.android.SwapView TS SwapView
// FQImport: com.lightningkite.khrysalis.views.ViewGenerator.generate TS generate
import { comLightningkiteKhrysalisObservablesObservablePropertySubscribeBy } from './../ObservableProperty.ext.shared'
import { getAndroidViewViewRemoved, ioReactivexDisposablesDisposableUntil } from './../../rx/DisposeCondition.actual'
import { ObservableStack } from './../ObservableStack.shared'
import { ViewGenerator } from './../../views/ViewGenerator.shared'
import { ViewDependency } from './../../views/ViewDependency.actual'
import { SubscriptionLike } from 'rxjs'

//! Declares com.lightningkite.khrysalis.observables.binding.bindStack
export function comLightningkiteKhrysalisViewsAndroidSwapViewBindStack(this_: SwapView, dependency: ViewDependency, obs: ObservableStack<ViewGenerator>): SubscriptionLike{
    let currentData = (()=>{const temp427 = obs.stack;
    (temp427[temp427.length - 1] ?? null)})();
    
    let currentStackSize = obs.stack.length;
    
    let currentView = currentData?.generate(dependency) ?: View.constructorandroidcontentContext(getComLightningkiteKhrysalisViewsAndroidSwapViewContext(this_));
    
    this_.addView(currentView, FrameLayout.LayoutParams.constructorkotlinInt, kotlinInt(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
    ioReactivexDisposablesDisposableUntil(comLightningkiteKhrysalisObservablesObservablePropertySubscribeBy(obs, undefined, undefined, (datas) => {
                this_.post(() => {
                        if (currentData.equals((datas[datas.length - 1] ?? null))) return
                        
                        const oldView = currentView;
                        
                        const oldStackSize = currentStackSize;
                        
                        
                        let newView = (()=>{const temp430 = obs.stack;
                        (temp430[temp430.length - 1] ?? null)})()?.generate(dependency);
                        
                        if (newView.equals(null)) {
                            newView = View.constructorandroidcontentContext(getComLightningkiteKhrysalisViewsAndroidSwapViewContext(this_));
                            setComLightningkiteKhrysalisViewsAndroidSwapViewVisibility(this_, View.GONE);
                        } else {
                            setComLightningkiteKhrysalisViewsAndroidSwapViewVisibility(this_, View.VISIBLE);
                        }
                        this_.addView(newView, FrameLayout.LayoutParams.constructorkotlinInt, kotlinInt(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                        const newStackSize = datas.length;
                        
                        
                        if (oldStackSize === 0){
                            oldView.animate().alpha(0f);
                            setAndroidViewViewAlpha(newView, 0f);
                            newView.animate().alpha(1f);
                        } else if (oldStackSize > newStackSize){
                            oldView.animate().translationX(getComLightningkiteKhrysalisViewsAndroidSwapViewWidth(this_));
                            setAndroidViewViewTranslationX(newView, -getComLightningkiteKhrysalisViewsAndroidSwapViewWidth(this_));
                            newView.animate().translationX(0f);
                        } else if (oldStackSize < newStackSize){
                            oldView.animate().translationX(-getComLightningkiteKhrysalisViewsAndroidSwapViewWidth(this_));
                            setAndroidViewViewTranslationX(newView, getComLightningkiteKhrysalisViewsAndroidSwapViewWidth(this_));
                            newView.animate().translationX(0f);
                        } else {
                            oldView.animate().alpha(0f);
                            setAndroidViewViewAlpha(newView, 0f);
                            newView.animate().alpha(1f);
                        }
                        oldView.animate().withEndAction(() => {
                                this_.removeView(oldView)
                        });
                        
                        currentData = (datas[datas.length - 1] ?? null);
                        currentView = newView;
                        currentStackSize = newStackSize;
                })
    }), getAndroidViewViewRemoved(this_));
    
}


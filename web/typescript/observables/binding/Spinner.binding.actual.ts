// Generated by Khrysalis TypeScript converter - this file will be overwritten.
// File: observables/binding/Spinner.binding.actual.kt
// Package: com.lightningkite.khrysalis.observables.binding
// FQImport: com.lightningkite.khrysalis.observables.binding.bind.<no name provided>.getView.convertView TS convertView
// FQImport: com.lightningkite.khrysalis.observables.binding.bind.<no name provided>.getView.view TS view
// FQImport: tag TS getAndroidViewViewTag
// FQImport: com.lightningkite.khrysalis.observables.MutableObservableProperty.value TS value
// FQImport: android.widget.Spinner TS Spinner
// FQImport: com.lightningkite.khrysalis.observables.binding.bind.<no name provided>.notifyDataSetChanged TS notifyDataSetChanged
// FQImport: com.lightningkite.khrysalis.observables.binding.bind.<no name provided>.getView.position TS position
// FQImport: adapter TS setAndroidWidgetSpinnerAdapter
// FQImport: com.lightningkite.khrysalis.observables.binding.bind.<no name provided>.getView.<anonymous>.subview TS subview
// FQImport: selectedItemPosition TS getAndroidWidgetSpinnerSelectedItemPosition
// FQImport: com.lightningkite.khrysalis.observables.subscribeBy TS comLightningkiteKhrysalisObservablesObservablePropertySubscribeBy
// FQImport: onItemSelectedListener TS setAndroidWidgetSpinnerOnItemSelectedListener
// FQImport: android.view.ViewGroup TS ViewGroup
// FQImport: com.lightningkite.khrysalis.observables.binding.bind.<no name provided>.onItemSelected.newValue TS newValue
// FQImport: com.lightningkite.khrysalis.observables.binding.bind.<no name provided>.getView.<anonymous>.event TS event
// FQImport: com.lightningkite.khrysalis.observables.binding.bind.options TS options
// FQImport: android.widget.BaseAdapter TS BaseAdapter
// FQImport: android.widget.Spinner.setSelection TS setSelection
// FQImport: com.lightningkite.khrysalis.observables.binding.bind.<no name provided>.getView.<anonymous>.it TS it
// FQImport: com.lightningkite.khrysalis.observables.binding.bind.<anonymous>.it TS it
// FQImport: android.view.View TS View
// FQImport: com.lightningkite.khrysalis.observables.binding.bind.T TS T
// FQImport: tag TS setAndroidViewViewTag
// FQImport: com.lightningkite.khrysalis.rx.until TS ioReactivexDisposablesDisposableUntil
// FQImport: java.lang.IllegalStateException TS IllegalStateException
// FQImport: android.widget.AdapterView TS AdapterView
// FQImport: com.lightningkite.khrysalis.observables.binding.bind.makeView TS makeView
// FQImport: com.lightningkite.khrysalis.observables.ObservableProperty TS ObservableProperty
// FQImport: com.lightningkite.khrysalis.observables.binding.bind.<no name provided>.getItemId.position TS position
// FQImport: android.widget.AdapterView.OnItemSelectedListener TS AdapterViewOnItemSelectedListener
// FQImport: kotlin.collections.List.indexOf TS indexOf
// FQImport: com.lightningkite.khrysalis.observables.binding.bind.selected TS selected
// FQImport: com.lightningkite.khrysalis.observables.binding.bind.<no name provided>.getItem.position TS position
// FQImport: com.lightningkite.khrysalis.observables.StandardObservableProperty TS StandardObservableProperty
// FQImport: com.lightningkite.khrysalis.rx.removed TS getAndroidViewViewRemoved
// FQImport: com.lightningkite.khrysalis.observables.binding.bind.<no name provided>.onItemSelected.position TS position
// FQImport: kotlin.collections.getOrNull TS kotlinCollectionsListGetOrNull
// FQImport: com.lightningkite.khrysalis.observables.StandardObservableProperty.value TS value
// FQImport: com.lightningkite.khrysalis.observables.binding.bind.<anonymous>.index TS index
// FQImport: com.lightningkite.khrysalis.observables.MutableObservableProperty TS MutableObservableProperty
// FQImport: com.lightningkite.khrysalis.observables.ObservableProperty.value TS value
import { comLightningkiteKhrysalisObservablesObservablePropertySubscribeBy } from './../ObservableProperty.ext.shared'
import { MutableObservableProperty } from './../MutableObservableProperty.shared'
import { getAndroidViewViewRemoved, ioReactivexDisposablesDisposableUntil } from './../../rx/DisposeCondition.actual'
import { StandardObservableProperty } from './../StandardObservableProperty.shared'
import { ObservableProperty } from './../ObservableProperty.shared'
import { tryCastClass } from 'khrysalis/dist/Kotlin'

//! Declares com.lightningkite.khrysalis.observables.binding.bind
export function androidWidgetSpinnerBind<T>(this_: Spinner, options: ObservableProperty<Array<T>>, selected: MutableObservableProperty<T>, makeView: (a: ObservableProperty<T>) => View): void{
    setAndroidWidgetSpinnerAdapter(this_, new class Anon extends BaseAdapter {
            public constructor() {
                super();
                comLightningkiteKhrysalisObservablesObservablePropertySubscribeBy(options, undefined, undefined, (_) => {
                        this.notifyDataSetChanged()
                });
            }
            
            
            
            getView(position: number, convertView: (View | null), parent: (ViewGroup | null)): View{
                const view = convertView ?: ((this_1) => {
                        const event = new StandardObservableProperty<T>(kotlinCollectionsListGetOrNull(options.value, position) ?: selected.value, undefined);
                        
                        const subview = makeView(event);
                        
                        setAndroidViewViewTag(subview, event);
                        return subview;
                })(this);
                
                ((_it)=>{
                        if(_it === null) return null;
                        return ((it) => {
                                it.value = kotlinCollectionsListGetOrNull(options.value, position) ?: selected.value
                        })(_it)
                })((tryCastClass(getAndroidViewViewTag(view), StandardObservableProperty))) ?: throw IllegalStateException.constructor();
                return view;
            }
            
            getItem(position: number): (any | null){ return kotlinCollectionsListGetOrNull(options.value, position); }
            getItemId(position: number): number{ return position; }
            getCount(): number{ return options.value.length; }
    }());
    ioReactivexDisposablesDisposableUntil(comLightningkiteKhrysalisObservablesObservablePropertySubscribeBy(selected, undefined, undefined, (it) => {
                const index = options.value.indexOf(it);
                
                if (!(index === -1) && !(index === getAndroidWidgetSpinnerSelectedItemPosition(this_))) {
                    this_.setSelection(index);
                }
    }), getAndroidViewViewRemoved(this_));
    setAndroidWidgetSpinnerOnItemSelectedListener(this_, new class Anon implements AdapterViewOnItemSelectedListener {
            public static implementsInterfaceAndroidWidgetAdapterViewOnItemSelectedListener = true;
            public constructor() {
            }
            
            onNothingSelected(parent: (AdapterView<*> | null)): void{}
            
            onItemSelected(parent: (AdapterView<*> | null), view: (View | null), position: number, id: number): void{
                const newValue = kotlinCollectionsListGetOrNull(options.value, position) ?: return;
                
                if (!(selected.value.equals(newValue))) {
                    selected.value = newValue;
                }
            }
    }());
}

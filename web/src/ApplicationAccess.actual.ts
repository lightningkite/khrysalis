import {ObservableProperty} from "./observables/ObservableProperty.shared";
import {ConstantObservableProperty} from "./observables/ConstantObservableProperty.shared";
import {MutableObservableProperty} from "./observables/MutableObservableProperty.shared";
import {xObservablePropertyWithWrite} from "./observables/WriteAddedObservableProperty.shared";

//! Declares com.lightningkite.khrysalis.ApplicationAccess
export class ApplicationAccess {
    public static INSTANCE = new ApplicationAccess();
    public foreground: ObservableProperty<boolean> = new ConstantObservableProperty(true)
    public softInputActive: MutableObservableProperty<boolean> = xObservablePropertyWithWrite(new ConstantObservableProperty(false), (x)=>{})
}
import {ObservableProperty} from "./observables/ObservableProperty.shared";
import {ConstantObservableProperty} from "./observables/ConstantObservableProperty.shared";


export class ApplicationAccess {
    public static INSTANCE = new ApplicationAccess();
    public foreground: ObservableProperty<boolean> = new ConstantObservableProperty(true)
    public softInputActive: ObservableProperty<boolean> = new ConstantObservableProperty(false)
}
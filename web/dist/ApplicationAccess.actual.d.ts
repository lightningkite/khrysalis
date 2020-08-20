import { ObservableProperty } from "./observables/ObservableProperty.shared";
import { MutableObservableProperty } from "./observables/MutableObservableProperty.shared";
export declare class ApplicationAccess {
    static INSTANCE: ApplicationAccess;
    foreground: ObservableProperty<boolean>;
    softInputActive: MutableObservableProperty<boolean>;
}

import { ObservableProperty } from "./observables/ObservableProperty.shared";
export declare class ApplicationAccess {
    static INSTANCE: ApplicationAccess;
    foreground: ObservableProperty<boolean>;
    softInputActive: ObservableProperty<boolean>;
}

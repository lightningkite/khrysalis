import { StandardObservableProperty } from "khrysalis/dist/observables/StandardObservableProperty.shared";
export declare class Notifications {
    static INSTANCE: Notifications;
    notificationToken: StandardObservableProperty<string | null>;
    request(): void;
}

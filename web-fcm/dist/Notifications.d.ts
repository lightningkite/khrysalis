import { StandardObservableProperty } from "khrysalis/dist/observables/StandardObservableProperty.shared";
import { ForegroundNotificationHandler } from "./ForegroundNotificationHandler.shared";
export declare class Notifications {
    static INSTANCE: Notifications;
    notificationToken: StandardObservableProperty<string | null>;
    handler: ForegroundNotificationHandler | null;
    fcmPublicKey?: string;
    request(firebaseAppName?: string): void;
}

import {StandardObservableProperty} from "khrysalis/dist/observables/StandardObservableProperty.shared";

export class Notifications {
    static INSTANCE = new Notifications();
    notificationToken = new StandardObservableProperty<string|null>(null);
    request(){
        Notification.requestPermission();
    }
}
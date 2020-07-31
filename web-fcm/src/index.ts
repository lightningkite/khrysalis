import {ViewGenerator} from "khrysalis/dist/views/ViewGenerator.shared";
import {main} from "khrysalis/dist/main";
import firebase from "firebase";
import {Notifications} from "./Notifications";
import {tryCastInterface} from "khrysalis/dist/kotlin/Language";
import {
    ForegroundNotificationHandler,
    ForegroundNotificationHandlerResult
} from "./ForegroundNotificationHandler.shared";

interface Payload {
    data: Record<string, string>
    notification: PayloadNotification
}
interface PayloadNotification extends NotificationOptions {
    title: string
}

export function mainWithFcm(rootVg: ViewGenerator, fcmPublicKey: string): void {
    main(rootVg);
    const messaging = firebase.messaging();
    messaging.usePublicVapidKey(fcmPublicKey);
    messaging.getToken().then((value) => {
        Notifications.INSTANCE.notificationToken.value = value;
    }).catch((err) => {
        console.warn('Unable to retrieve refreshed token ', err);
    });
    messaging.onTokenRefresh(() => {
        messaging.getToken().then((value) => {
            Notifications.INSTANCE.notificationToken.value = value;
        }).catch((err) => {
            console.warn('Unable to retrieve refreshed token ', err);
        });
    });
    messaging.onMessage((payload: Payload) => {
        let handler = tryCastInterface<ForegroundNotificationHandler>(rootVg, "ForegroundNotificationHandler")
        let data: Map<string, string>;
        let payData = payload.data;
        if (payData) {
            data = new Map(Object.entries(payData))
        } else {
            data = new Map();
        }

        let handledState = handler?.handleNotificationInForeground(data);
        if (handledState != ForegroundNotificationHandlerResult.SUPPRESS_NOTIFICATION) {
            new Notification(payload.notification.title, payload.notification)
        }
    });
}
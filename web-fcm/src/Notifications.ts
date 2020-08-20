import {StandardObservableProperty} from "khrysalis/dist/observables/StandardObservableProperty.shared";
import {ViewGenerator} from "khrysalis/dist/views/ViewGenerator.shared";
import firebase from "firebase";
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

//! Declares com.lightningkite.khrysalis.fcm.Notifications
export class Notifications {
    static INSTANCE = new Notifications();
    notificationToken = new StandardObservableProperty<string|null>(null);
    handler: ForegroundNotificationHandler | null = null
    fcmPublicKey?: string
    request(firebaseAppName?: string){
        Notification.requestPermission().then((x)=>{
            if(x == "granted"){
                const messaging = firebase.messaging(firebase.app());
                messaging.usePublicVapidKey(this.fcmPublicKey as string);
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
                    let data: Map<string, string>;
                    let payData = payload.data;
                    if (payData) {
                        data = new Map(Object.entries(payData))
                    } else {
                        data = new Map();
                    }

                    let handledState = this.handler?.handleNotificationInForeground(data);
                    if (handledState != ForegroundNotificationHandlerResult.SUPPRESS_NOTIFICATION) {
                        new Notification(payload.notification.title, payload.notification)
                    }
                });
            }
        });
    }
}
import {ViewGenerator} from "khrysalis/dist/views/ViewGenerator.shared";
import {main} from "khrysalis/dist/main";
import firebase from "firebase";
import {Notifications} from "./Notifications";
import {tryCastInterface} from "khrysalis/dist/kotlin/Language";
import {
    ForegroundNotificationHandler,
    ForegroundNotificationHandlerResult
} from "./ForegroundNotificationHandler.shared";

export function mainWithFcm(rootVg: ViewGenerator, fcmPublicKey?: string): void {
    main(rootVg);
    Notifications.INSTANCE.handler = tryCastInterface<ForegroundNotificationHandler>(rootVg, "ForegroundNotificationHandler");
    if(fcmPublicKey){
        Notifications.INSTANCE.fcmPublicKey = fcmPublicKey;
    }
}
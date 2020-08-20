"use strict";
var __importDefault = (this && this.__importDefault) || function (mod) {
    return (mod && mod.__esModule) ? mod : { "default": mod };
};
Object.defineProperty(exports, "__esModule", { value: true });
const StandardObservableProperty_shared_1 = require("khrysalis/dist/observables/StandardObservableProperty.shared");
const firebase_1 = __importDefault(require("firebase"));
const ForegroundNotificationHandler_shared_1 = require("./ForegroundNotificationHandler.shared");
//! Declares com.lightningkite.khrysalis.fcm.Notifications
class Notifications {
    constructor() {
        this.notificationToken = new StandardObservableProperty_shared_1.StandardObservableProperty(null);
        this.handler = null;
    }
    request(firebaseAppName) {
        Notification.requestPermission().then((x) => {
            if (x == "granted") {
                const messaging = firebase_1.default.messaging(firebase_1.default.app());
                messaging.usePublicVapidKey(this.fcmPublicKey);
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
                messaging.onMessage((payload) => {
                    var _a;
                    let data;
                    let payData = payload.data;
                    if (payData) {
                        data = new Map(Object.entries(payData));
                    }
                    else {
                        data = new Map();
                    }
                    let handledState = (_a = this.handler) === null || _a === void 0 ? void 0 : _a.handleNotificationInForeground(data);
                    if (handledState != ForegroundNotificationHandler_shared_1.ForegroundNotificationHandlerResult.SUPPRESS_NOTIFICATION) {
                        new Notification(payload.notification.title, payload.notification);
                    }
                });
            }
        });
    }
}
exports.Notifications = Notifications;
Notifications.INSTANCE = new Notifications();
//# sourceMappingURL=Notifications.js.map
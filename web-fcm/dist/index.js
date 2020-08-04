"use strict";
var __importDefault = (this && this.__importDefault) || function (mod) {
    return (mod && mod.__esModule) ? mod : { "default": mod };
};
Object.defineProperty(exports, "__esModule", { value: true });
const main_1 = require("khrysalis/dist/main");
const firebase_1 = __importDefault(require("firebase"));
const Notifications_1 = require("./Notifications");
const Language_1 = require("khrysalis/dist/kotlin/Language");
const ForegroundNotificationHandler_shared_1 = require("./ForegroundNotificationHandler.shared");
function mainWithFcm(rootVg, fcmPublicKey) {
    main_1.main(rootVg);
    const messaging = firebase_1.default.messaging();
    messaging.usePublicVapidKey(fcmPublicKey);
    messaging.getToken().then((value) => {
        Notifications_1.Notifications.INSTANCE.notificationToken.value = value;
    }).catch((err) => {
        console.warn('Unable to retrieve refreshed token ', err);
    });
    messaging.onTokenRefresh(() => {
        messaging.getToken().then((value) => {
            Notifications_1.Notifications.INSTANCE.notificationToken.value = value;
        }).catch((err) => {
            console.warn('Unable to retrieve refreshed token ', err);
        });
    });
    messaging.onMessage((payload) => {
        let handler = Language_1.tryCastInterface(rootVg, "ForegroundNotificationHandler");
        let data;
        let payData = payload.data;
        if (payData) {
            data = new Map(Object.entries(payData));
        }
        else {
            data = new Map();
        }
        let handledState = handler === null || handler === void 0 ? void 0 : handler.handleNotificationInForeground(data);
        if (handledState != ForegroundNotificationHandler_shared_1.ForegroundNotificationHandlerResult.SUPPRESS_NOTIFICATION) {
            new Notification(payload.notification.title, payload.notification);
        }
    });
}
exports.mainWithFcm = mainWithFcm;
//# sourceMappingURL=index.js.map
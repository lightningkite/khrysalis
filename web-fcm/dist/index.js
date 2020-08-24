"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
const main_1 = require("khrysalis/dist/main");
const Notifications_1 = require("./Notifications");
const Language_1 = require("khrysalis/dist/kotlin/Language");
function mainWithFcm(rootVg, fcmPublicKey) {
    main_1.main(rootVg);
    Notifications_1.Notifications.INSTANCE.handler = Language_1.tryCastInterface(rootVg, "ForegroundNotificationHandler");
    if (fcmPublicKey) {
        Notifications_1.Notifications.INSTANCE.fcmPublicKey = fcmPublicKey;
    }
}
exports.mainWithFcm = mainWithFcm;
//# sourceMappingURL=index.js.map
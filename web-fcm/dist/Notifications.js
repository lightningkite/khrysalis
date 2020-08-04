"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
const StandardObservableProperty_shared_1 = require("khrysalis/dist/observables/StandardObservableProperty.shared");
class Notifications {
    constructor() {
        this.notificationToken = new StandardObservableProperty_shared_1.StandardObservableProperty(null);
    }
    request() {
        Notification.requestPermission();
    }
}
exports.Notifications = Notifications;
Notifications.INSTANCE = new Notifications();
//# sourceMappingURL=Notifications.js.map
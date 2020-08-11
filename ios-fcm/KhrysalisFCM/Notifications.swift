//
//  Notifications.swift
//  KhrysalisFCM
//
//  Created by Joseph Ivie on 1/21/20.
//  Copyright © 2020 Lightning Kite. All rights reserved.
//

import Foundation
import FirebaseCore
import FirebaseMessaging
import Khrysalis


public class Notifications {
    static public let INSTANCE = Notifications()

    public var notificationToken = StandardObservableProperty<String?>(underlyingValue: nil)
    public func request(firebaseAppName: String? = nil){
        UNUserNotificationCenter.current().requestAuthorization(options: [.alert, .sound, .badge], completionHandler: { (success, error) in
            if success {
                if let firebaseAppName = firebaseAppName, let app = FirebaseApp.app(name: firebaseAppName), let current = FirebaseApp.app() {
                    current.delete { (x) in
                        FirebaseApp.configure(options: app.options)
                        Notifications.INSTANCE.notificationToken.value = Messaging.messaging().fcmToken
                    }
                } else {
                    Notifications.INSTANCE.notificationToken.value = Messaging.messaging().fcmToken
                }
            }
        })
    }
    public func configure(){
        request()
    }
}

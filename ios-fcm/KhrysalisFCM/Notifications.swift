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


public enum Notifications {
    static public var notificationToken = StandardObservableProperty<String?>(nil)
    static public func configure(){
        UNUserNotificationCenter.current().requestAuthorization(options: [.alert, .sound, .badge], completionHandler: { (success, error) in
            if success {
                Notifications.notificationToken.value = Messaging.messaging().fcmToken
            }
        })
    }
}

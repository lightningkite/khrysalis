//
//  KhrysalisFcmAppDelegate.swift
//  KhrysalisFCM
//
//  Created by Joseph Ivie on 1/21/20.
//  Copyright © 2020 Lightning Kite. All rights reserved.
//

import Foundation
import FirebaseMessaging
import FirebaseCore
import Khrysalis

open class KhrysalisFcmAppDelegate: KhrysalisAppDelegate, UNUserNotificationCenterDelegate, MessagingDelegate {

    override open func application(_ application: UIApplication, didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey: Any]?) -> Bool {
        FirebaseApp.configure()
        Messaging.messaging().delegate = self
        UNUserNotificationCenter.current().delegate = self

        let _ = super.application(application, didFinishLaunchingWithOptions: launchOptions)

        UIApplication.shared.registerForRemoteNotifications()

        //Handle tapped notification from launch
        if let notification = launchOptions?[UIApplication.LaunchOptionsKey.remoteNotification] {
            onNotificationClick(userInfo: notification as! Dictionary<AnyHashable, Any>)
        }

        return true
    }

    // This method will be called when app received push notifications in foreground
    public func userNotificationCenter(_ center: UNUserNotificationCenter, willPresent notification: UNNotification, withCompletionHandler completionHandler: @escaping (UNNotificationPresentationOptions) -> Void)
    {
        DispatchQueue.main.async {
            print("Got notification in foreground: \(notification.request.content.userInfo)")
            
            var shouldShow: ForegroundNotificationHandlerResult = .UNHANDLED
            if let entryPoint = self.viewController?.main as? ForegroundNotificationHandler {
                let info = notification.request.content.userInfo
                shouldShow = entryPoint.handleNotificationInForeground(map:
                    Dictionary(uniqueKeysWithValues:
                        info
                            .filter { it in it.key is String && it.value is String }
                            .map { ($0.key as! String, $0.value as! String) }
                    )
                )
            }
            print("Notification was: \(shouldShow)")
            if shouldShow == .SUPPRESS_NOTIFICATION {
                completionHandler([])
            } else {
                completionHandler([.alert, .badge, .sound])
            }
        }
    }

    //This method will be called when a user taps on a notification and the app is running.
    public func userNotificationCenter(_ center: UNUserNotificationCenter,
                                    didReceive response: UNNotificationResponse,
                                    withCompletionHandler completionHandler: @escaping () -> Void) {
        let userInfo = response.notification.request.content.userInfo
        onNotificationClick(userInfo: userInfo)
        completionHandler()
    }

    private func onNotificationClick(userInfo: [AnyHashable: Any]){
        if let urlString = userInfo["deepLink"] as? String,
            let url = URL(string: urlString),
            let components = URLComponents(url: url, resolvingAgainstBaseURL: false)
        {
            var items: Dictionary<String, String> = [:]
            for item in components.queryItems ?? [] {
                items[item.name] = item.value
            }
            if let main = main as? EntryPoint {
                main.handleDeepLink(
                    schema: components.scheme ?? "",
                    host: components.host ?? "",
                    path: components.path,
                    params: items
                )
            }
        }
    }

    public func messaging(_ messaging: Messaging, didReceiveRegistrationToken fcmToken: String) {
        Notifications.INSTANCE.notificationToken.value = fcmToken
    }
}

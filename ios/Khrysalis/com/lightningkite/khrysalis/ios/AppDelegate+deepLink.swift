//
//  AppDelegate+deepLink.swift
//  Khrysalis
//
//  Created by Joseph Ivie on 10/24/19.
//  Copyright © 2019 Lightning Kite. All rights reserved.
//

import Foundation
import UIKit

public protocol UIApplicationDelegatePlusDeepLink: UIApplicationDelegate {
    func handleDeepLink(schema: String, host: String, path: String, params: Dictionary<String, String>)
    func application(_ app: UIApplication, open url: URL, options: [UIApplication.OpenURLOptionsKey : Any]) -> Bool
}

public extension UIApplicationDelegatePlusDeepLink {
    func application(_ app: UIApplication, open url: URL, options: [UIApplication.OpenURLOptionsKey : Any]) -> Bool {
        guard let components = URLComponents(url: url, resolvingAgainstBaseURL: false) else { return false }
        var items: Dictionary<String, String> = [:]
        for item in components.queryItems ?? [] {
            items[item.name] = item.value
        }
        handleDeepLink(
            schema: components.scheme ?? "",
            host: components.host ?? "",
            path: components.path,
            params: items
        )
        return true
    }
}

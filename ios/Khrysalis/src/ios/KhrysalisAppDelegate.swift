//
//  AppDelegate.swift
//
//  Created by Joseph Ivie on 2/18/19.
//  Copyright © 2019 Lightning Kite. All rights reserved.
//

import UIKit

open class KhrysalisAppDelegate: UIResponder, UIApplicationDelegate {

    public var window: UIWindow?
    public var viewController: KhrysalisViewController?
    open var main: ViewGenerator?
    
    open var keychainAccessGroup: String? {
        return nil
    }
    open func makeViewController() -> KhrysalisViewController {
        let vc = makeMain()
        main = vc
        return KhrysalisViewController(vc)
    }
    open func makeMain() -> ViewGenerator {
        fatalError("Not implemented")
    }
    
    open func application(_ app: UIApplication, open url: URL, options: [UIApplication.OpenURLOptionsKey : Any] = [:]) -> Bool {
        guard let components = URLComponents(url: url, resolvingAgainstBaseURL: false) else { return false }
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
        return true
    }

    open func application(_ application: UIApplication, didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey: Any]?) -> Bool {
        
        UIView.useLayoutSubviewsLambda()
        
        // Override point for customization after application launch.
        if let keychainAccessGroup = keychainAccessGroup {
            SecurePreferences.setKeychainAccessGroup(keychainAccessGroup)
        }
        
        window = UIWindow(frame: UIScreen.main.bounds)
        let nav = SpecialNavController()
        let vc = makeViewController()
        nav.viewControllers = [vc]
        viewController = vc
        window?.rootViewController = nav
        window?.makeKeyAndVisible()
        
        return true
    }

    open func applicationWillResignActive(_ application: UIApplication) {
        // Sent when the application is about to move from active to inactive state. This can occur for certain types of temporary interruptions (such as an incoming phone call or SMS message) or when the user quits the application and it begins the transition to the background state.
        // Use this method to pause ongoing tasks, disable timers, and invalidate graphics rendering callbacks. Games should use this method to pause the game.
    }

    open func applicationDidEnterBackground(_ application: UIApplication) {
        // Use this method to release shared resources, save user data, invalidate timers, and store enough application state information to restore your application to its current state in case it is terminated later.
        // If your application supports background execution, this method is called instead of applicationWillTerminate: when the user quits.
        ApplicationAccess.INSTANCE.applicationIsActiveEvent.onNext(false)
    }

    open func applicationWillEnterForeground(_ application: UIApplication) {
        // Called as part of the transition from the background to the active state; here you can undo many of the changes made on entering the background.
        ApplicationAccess.INSTANCE.applicationIsActiveEvent.onNext(true)
    }

    open func applicationDidBecomeActive(_ application: UIApplication) {
        // Restart any tasks that were paused (or not yet started) while the application was inactive. If the application was previously in the background, optionally refresh the user interface.
    }

    open func applicationWillTerminate(_ application: UIApplication) {
        // Called when the application is about to terminate. Save data if appropriate. See also applicationDidEnterBackground:.
    }

    
}


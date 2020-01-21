//Package: com.lightningkite.kwift.views
//Converted using Kwift2

import Foundation
import RxSwift
import RxRelay



public protocol EntryPoint {
    
    func handleDeepLink(schema: String, host: String, path: String, params: Dictionary<String, String>) -> Void
    func handleDeepLink(_ schema: String, _ host: String, _ path: String, _ params: Dictionary<String, String>) -> Void
    
    func handleNotificationInForeground(map: Dictionary<String, String>) -> Bool
    func handleNotificationInForeground(_ map: Dictionary<String, String>) -> Bool
    
    func onBackPressed() -> Bool
    
    var mainStack: ObservableStack<ViewGenerator>?  { get }
}

public extension EntryPoint {
    
    func handleDeepLink(schema: String, host: String, path: String, params: Dictionary<String, String>) -> Void {
        print("Empty handler; \(schema)://\(host)/\(path)/\(params)")
    }
    func handleDeepLink(_ schema: String, _ host: String, _ path: String, _ params: Dictionary<String, String>) -> Void {
        return handleDeepLink(schema: schema, host: host, path: path, params: params)
    }
    
    func handleNotificationInForeground(map: Dictionary<String, String>) -> Bool {
        print("Received notification in foreground with \(map)")
        return true
    }
    func handleNotificationInForeground(_ map: Dictionary<String, String>) -> Bool {
        return handleNotificationInForeground(map: map)
    }
    
    func onBackPressed() -> Bool {
        return false
    }
    public var mainStack: ObservableStack<ViewGenerator>?  {
        get {
            return nil
        }
    }
}
 

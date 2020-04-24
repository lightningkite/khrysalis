//Package: com.lightningkite.khrysalis.views
//Converted using Khrysalis2

import Foundation
import Khrysalis
import RxSwift
import RxRelay



public protocol EntryPoint {
    
    func handleDeepLink(schema: String, host: String, path: String, params: Dictionary<String, String>) -> Void
    func handleDeepLink(_ schema: String, _ host: String, _ path: String, _ params: Dictionary<String, String>) -> Void
    
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
    
    func onBackPressed() -> Bool {
        return false
    }
    public var mainStack: ObservableStack<ViewGenerator>?  {
        get {
            return nil
        }
    }
}
 

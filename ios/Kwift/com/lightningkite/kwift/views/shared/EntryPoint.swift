//Package: com.lightningkite.kwift.views.shared
//Converted using Kwift2

import Foundation



public protocol EntryPoint {
    
    func handleDeepLink(schema: String, host: String, path: String, params: Dictionary<String, String>) -> Void
    func handleDeepLink(_ schema: String, _ host: String, _ path: String, _ params: Dictionary<String, String>) -> Void
    
    func onBackPressed() -> Bool
    
    var mainStack: ObservableStack<ViewGenerator>?  { get }
}

public extension EntryPoint {
    
    func handleDeepLink(schema: String, host: String, path: String, params: Dictionary<String, String>) -> Void {
        print("Empty handler")
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
 

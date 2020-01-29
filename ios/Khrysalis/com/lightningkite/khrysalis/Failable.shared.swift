//Package: com.lightningkite.khrysalis
//Converted using Khrysalis2

import Foundation
import RxSwift
import RxRelay



public class Failable<T> {
    
    public var result: T? 
    public var issue: String? 
    
    
    //Start Companion
    
    static public func failure<T>(message: String) -> Failable<T> {
        return Failable<T>(issue: message)
    }
    static public func failure<T>(_ message: String) -> Failable<T> {
        return failure(message: message)
    }
    
    static public func success<T>(value: T) -> Failable<T> {
        return Failable<T>(result: value)
    }
    static public func success<T>(_ value: T) -> Failable<T> {
        return success(value: value)
    }
    //End Companion
    
    
    public init(result: T?  = nil, issue: String?  = nil) {
        self.result = result
        self.issue = issue
    }
    convenience public init(_ result: T? , _ issue: String?  = nil) {
        self.init(result: result, issue: issue)
    }
}
 

// Generated by Khrysalis Swift converter - this file will be overwritten.
// File: views/HasBackAction.shared.kt
// Package: com.lightningkite.khrysalis.views
import Foundation

public protocol HasBackAction: AnyObject {
    
    func onBackPressed() -> Bool 
}
public extension HasBackAction {
    func onBackPressed() -> Bool { return false }
}

//Package: com.lightningkite.khrysalis.fcm
//Converted using Khrysalis2

import Foundation
import RxSwift
import RxRelay
import Khrysalis



public protocol ForegroundNotificationHandler: class {
    
    func handleNotificationInForeground(map: Dictionary<String, String>) -> ForegroundNotificationHandlerResult
    func handleNotificationInForeground(_ map: Dictionary<String, String>) -> ForegroundNotificationHandlerResult
}

public extension ForegroundNotificationHandler {
    
    func handleNotificationInForeground(map: Dictionary<String, String>) -> ForegroundNotificationHandlerResult {
        print("Received notification in foreground with \(map)")
        return ForegroundNotificationHandlerResult.SHOW_NOTIFICATION
    }
    func handleNotificationInForeground(_ map: Dictionary<String, String>) -> ForegroundNotificationHandlerResult {
        return handleNotificationInForeground(map: map)
    }
}
 
 

public enum ForegroundNotificationHandlerResult: String, StringEnum, CaseIterable, Codable {
    case SUPPRESS_NOTIFICATION = "SUPPRESS_NOTIFICATION"
    case SHOW_NOTIFICATION = "SHOW_NOTIFICATION"
    case UNHANDLED = "UNHANDLED"
    public init(from decoder: Decoder) throws {
        self = try ForegroundNotificationHandlerResult(rawValue: decoder.singleValueContainer().decode(RawValue.self)) ?? .SUPPRESS_NOTIFICATION
    }
}
 

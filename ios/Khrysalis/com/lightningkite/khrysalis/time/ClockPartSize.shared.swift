//Package: com.lightningkite.khrysalis.time
//Converted using Khrysalis2

import Foundation
import RxSwift
import RxRelay



public enum ClockPartSize: String, StringEnum, CaseIterable, Codable {
    case None = "None"
    case Short = "Short"
    case Medium = "Medium"
    case Long = "Long"
    case Full = "Full"
    public init(from decoder: Decoder) throws {
        self = try ClockPartSize(rawValue: decoder.singleValueContainer().decode(RawValue.self)) ?? .None
    }
}
 

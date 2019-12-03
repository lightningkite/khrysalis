//Package: com.lightningkite.kwift.shared
//Converted using Kwift2

import Foundation



public final class DateAlone: Equatable, Hashable, Codable {
    
    
    public init(from decoder: Decoder) throws {
        let string: String = try decoder.singleValueContainer().decode(String.self)
        year = string.substringBefore("-").toInt()
        month = string.substringAfter("-").substringBefore("-").toInt()
        day = string.substringAfterLast("-").toInt()
    }
    
    public var year: Int32
    public var month: Int32
    public var day: Int32
    public static func == (lhs: DateAlone, rhs: DateAlone) -> Bool {
        return lhs.year == rhs.year &&
            lhs.month == rhs.month &&
            lhs.day == rhs.day
    }
    
    public func iso8601() -> String {
        let formatter = DateFormatter()
        formatter.locale = Locale(identifier: "en_US_POSIX")
        formatter.dateFormat = "yyyy-MM-dd"
        return formatter.string(from: dateFrom(self, Date().timeAlone))
    }
    
    public func hash(into hasher: inout Hasher) {
        hasher.combine(year)
        hasher.combine(month)
        hasher.combine(day)
    }
    public func copy(
        year: (Int32)? = nil,
        month: (Int32)? = nil,
        day: (Int32)? = nil
    ) -> DateAlone {
        return DateAlone(
            year: year ?? self.year,
            month: month ?? self.month,
            day: day ?? self.day
        )
    }
    
    public func iso() -> String {
        return "\(year)-\(month)-\(day)"
    }
    
    
    public init(year: Int32, month: Int32, day: Int32) {
        self.year = year
        self.month = month
        self.day = day
    }
    convenience public init(_ year: Int32, _ month: Int32, _ day: Int32) {
        self.init(year: year, month: month, day: day)
    }
    
}
 
 

public final class TimeAlone: Equatable, Hashable, Codable {
    
    public init(from decoder: Decoder) throws {
        let string: String = try decoder.singleValueContainer().decode(String.self)
        hour = string.substringBefore(":").toInt()
        minute = string.substringAfter(":").substringBefore(":").toInt()
        second = string.substringAfterLast(":").toInt()
    }
    
    public func iso8601() -> String {
        let formatter = DateFormatter()
        formatter.locale = Locale(identifier: "en_US_POSIX")
        formatter.dateFormat = "HH:mm:ssZZZZZ"
        return formatter.string(from: dateFrom(Date().dateAlone, self))
    }
    
    public var hour: Int32
    public var minute: Int32
    public var second: Int32
    public static func == (lhs: TimeAlone, rhs: TimeAlone) -> Bool {
        return lhs.hour == rhs.hour &&
            lhs.minute == rhs.minute &&
            lhs.second == rhs.second
    }
    public func hash(into hasher: inout Hasher) {
        hasher.combine(hour)
        hasher.combine(minute)
        hasher.combine(second)
    }
    public func copy(
        hour: (Int32)? = nil,
        minute: (Int32)? = nil,
        second: (Int32)? = nil
    ) -> TimeAlone {
        return TimeAlone(
            hour: hour ?? self.hour,
            minute: minute ?? self.minute,
            second: second ?? self.second
        )
    }
    
    
    public init(hour: Int32, minute: Int32, second: Int32) {
        self.hour = hour
        self.minute = minute
        self.second = second
    }
    convenience public init(_ hour: Int32, _ minute: Int32, _ second: Int32) {
        self.init(hour: hour, minute: minute, second: second)
    }
    
}
 
 

public enum ClockPartSize: String, CaseIterable, Codable {
    case None = "None"
    case Short = "Short"
    case Medium = "Medium"
    case Long = "Long"
    case Full = "Full"
    public init(from decoder: Decoder) throws {
        self = try ClockPartSize(rawValue: decoder.singleValueContainer().decode(RawValue.self)) ?? .None
    }
}
 

//Package: com.lightningkite.kwift.shared
//Converted using Kwift2

import Foundation



public class DateAlone: Equatable, Hashable {
    
    public var year: Int32
    public var month: Int32
    public var day: Int32
    
    public static func == (lhs: DateAlone, rhs: DateAlone) -> Bool {
        return lhs.year == rhs.year &&
            lhs.month == rhs.month &&
            lhs.day == rhs.day
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
    
    
    //Start Companion
    
    static public func now() -> DateAlone {
        return Date().dateAlone
    }
    static public var farPast = DateAlone(-99999, 1, 1)
    static public var farFuture = DateAlone(99999, 12, 31)
    
    static public func iso(string: String) -> DateAlone {
        return DateAlone(string.substringBefore("-").toInt(), string.substringAfter("-").substringBefore("-").toInt(), string.substringAfterLast("-").toInt())
    }
    static public func iso(_ string: String) -> DateAlone {
        return iso(string: string)
    }
    //End Companion
    
    public var comparable: Int32 {
        get {
            return self.year * 12 * 31 + self.month * 31 + self.day
        }
    }
    public var dayOfWeek: Int32 {
        get {
            return dateFrom(self, TimeAlone.noon).dayOfWeek
        }
    }
    
    public func set(other: DateAlone) -> DateAlone {
        self.year = other.year
        self.month = other.month
        self.day = other.day
        return self
    }
    public func set(_ other: DateAlone) -> DateAlone {
        return set(other: other)
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
 
 

public class TimeAlone: Equatable, Hashable {
    
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
    
    
    //Start Companion
    
    static public func now() -> TimeAlone {
        return Date().timeAlone
    }
    
    static public func iso(string: String) -> TimeAlone {
        return TimeAlone(string.substringBefore(":").toInt(), string.substringAfter(":").substringBefore(":").toInt(), string.substringAfterLast(":").toInt())
    }
    static public func iso(_ string: String) -> TimeAlone {
        return iso(string: string)
    }
    static public var min = TimeAlone(0, 0, 0)
    static public var midnight = min
    static public var noon = TimeAlone(12, 0, 0)
    static public var max = TimeAlone(23, 59, 59)
    //End Companion
    
    public var comparable: Int32 {
        get {
            return self.hour * 60 * 60 + self.minute * 60 + self.second
        }
    }
    public var secondsInDay: Int32 {
        get {
            return self.hour * 60 * 60 + self.minute * 60 + self.second
        }
    }
    
    public func normalize() -> Void {
        hour = ( hour + minute.floorDiv(60) ).floorMod(24)
        minute = ( minute + second.floorDiv(60) ).floorMod(60)
        second = second.floorMod(60)
    }
    
    public func set(other: TimeAlone) -> TimeAlone {
        self.hour = other.hour
        self.minute = other.minute
        self.second = other.second
        return self
    }
    public func set(_ other: TimeAlone) -> TimeAlone {
        return set(other: other)
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
 
 

extension DateAlone {
    public func format(clockPartSize: ClockPartSize) -> String {
        return dateFrom(self, TimeAlone.noon).format(clockPartSize, ClockPartSize.None)
    }
    public func format(_ clockPartSize: ClockPartSize) -> String {
        return format(clockPartSize: clockPartSize)
    }
}
 

extension TimeAlone {
    public func format(clockPartSize: ClockPartSize) -> String {
        return dateFrom(Date().dateAlone, self).format(ClockPartSize.None, clockPartSize)
    }
    public func format(_ clockPartSize: ClockPartSize) -> String {
        return format(clockPartSize: clockPartSize)
    }
}
 

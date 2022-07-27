//
//  DateExtensions.swift
//  KhrysalisRuntime
//
//  This uses DateComponents to recreate Java 8 time, to some extent at least.
//  Priority was given to making this feel "Swift-y" as opposed to matching Java exactly.
//
//  Created by Joseph Ivie on 11/6/21.
//

import Foundation

private let eraBasis = DateComponents(era: 1, year: 1970, month: 1, day: 1, hour: 0, minute: 0, second: 0, nanosecond: 0)

private extension DateComponents {
    var existingComponents: Set<Calendar.Component> {
        var components: Set<Calendar.Component> = []
        if(calendar != nil) { components.insert(.calendar) }
        if(timeZone != nil) { components.insert(.timeZone) }
        if(era != nil) { components.insert(.era) }
        if(year != nil) { components.insert(.year) }
        if(month != nil) { components.insert(.month) }
        if(day != nil) { components.insert(.day) }
        if(hour != nil) { components.insert(.hour) }
        if(minute != nil) { components.insert(.minute) }
        if(second != nil) { components.insert(.second) }
        if(nanosecond != nil) { components.insert(.nanosecond) }
        return components
    }
    static func +(lhs: DateComponents, rhs: DateComponents) -> DateComponents {
        return lhs.calendar!.dateComponents(byAdding: rhs, to: lhs)!
    }
}
public protocol HasDateComponentsWithDay: HasDateComponents { }
public extension HasDateComponentsWithDay {
    var weekday: Int { get { dateComponents.weekday ?? dateComponents.calendar!.dateComponents([.weekday], from: dateComponents.calendar!.date(from: dateComponents)!).weekday! } }
}
private extension Calendar {
    func dateComponents(byAdding: DateComponents, to: DateComponents) -> DateComponents? {
        let originalDate = date(from: to)
        let addedDate = originalDate.flatMap { date(byAdding: byAdding, to: $0) }
        let components = addedDate.map { dateComponents(to.existingComponents, from: $0) }
        return components
    }
}
public protocol HasDateComponents: Comparable {
    init(from: Date)
    var dateComponents: DateComponents { get set }
}
extension DateComponents: HasDateComponents {
    public init(from: Date) {
        self = Calendar.current.dateComponents([.calendar, .era, .year, .month, .day, .hour, .minute, .second, .nanosecond], from: from)
    }
    public var dateComponents: DateComponents { get { self } set { self = newValue }}
}
public extension HasDateComponents {
    static func < (lhs: Self, rhs: Self) -> Bool {
        let left = (lhs.dateComponents.calendar ?? Calendar.current).date(from: lhs.dateComponents)!
        let right = (rhs.dateComponents.calendar ?? Calendar.current).date(from: rhs.dateComponents)!
        return left < right
    }
    func format(_ formatter: DateFormatter) -> String {
        let cal = dateComponents.calendar ?? Calendar.current
        if let date = cal.date(from: self.dateComponents) {
            return formatter.string(from: date)
        } else {
            return "-"
        }
    }
}
private extension HasDateComponents {
    mutating func absorb(other: DateComponents) {
        self.dateComponents = DateComponents(
            calendar: other.calendar ?? self.dateComponents.calendar,
            timeZone: other.timeZone ?? self.dateComponents.timeZone,
            era: other.era ?? self.dateComponents.era,
            year: other.year ?? self.dateComponents.year,
            month: other.month ?? self.dateComponents.month,
            day: other.day ?? self.dateComponents.day,
            hour: other.hour ?? self.dateComponents.hour,
            minute: other.minute ?? self.dateComponents.minute,
            second: other.second ?? self.dateComponents.second,
            nanosecond: other.nanosecond ?? self.dateComponents.nanosecond
        )
    }
}

public struct LocalDate: HasDateComponentsWithDay, Codable, Hashable {
    public init(from: Date = Date()) {
        self.init(calendar: Calendar.current, from: from)
    }
    public init(from decoder: Decoder) throws {
        let container = try decoder.singleValueContainer()
        let str = try container.decode(String.self)
        let parts = str.split(separator: "-")
        guard let year = Int(parts[0]) else { throw DecodingError.dataCorrupted(DecodingError.Context(codingPath: decoder.codingPath, debugDescription: "Year missing")) }
        guard let month = Int(parts[1]) else { throw DecodingError.dataCorrupted(DecodingError.Context(codingPath: decoder.codingPath, debugDescription: "Month missing")) }
        guard let day = Int(parts[2]) else { throw DecodingError.dataCorrupted(DecodingError.Context(codingPath: decoder.codingPath, debugDescription: "Day missing")) }
        self = LocalDate(year: year, month: month, day: day)
    }
    public func encode(to encoder: Encoder) throws {
        var container = encoder.singleValueContainer()
        try container.encode(String(format: "%04d-%02d-%02d", year, month, day))
    }
    
    static public let MIN = LocalDate(calendar: Calendar.current, era: 1, year: 0, month: 1, day: 1)
    static public let MAX = LocalDate(calendar: Calendar.current, era: 1, year: 9999, month: 12, day: 31)
    public var dateComponents: DateComponents
    public var calendar: Calendar { get { dateComponents.calendar! } set { dateComponents.calendar = newValue } }
    public var era: Int { get { dateComponents.era! } set { dateComponents.era = newValue } }
    public var year: Int { get { dateComponents.year! } set { dateComponents.year = newValue } }
    public var month: Int { get { dateComponents.month! } set { dateComponents.month = newValue } }
    public var day: Int { get { dateComponents.day! } set { dateComponents.day = newValue } }
    public var epochDay: Int { get { Int(Calendar.current.dateComponents([.day], from: Calendar.current.date(from: eraBasis)!, to: Calendar.current.date(from: self.dateComponents)!).day!) } }
    
    public init(
        calendar: Calendar,
        from: Date = Date()
    ) {
        dateComponents = calendar.dateComponents([.era, .year, .month, .day], from: from)
        self.calendar = calendar
    }
    public init(
        calendar: Calendar = Calendar.current,
        era: Int = 1,
        year: Int,
        month: Int,
        day: Int
    ) {
        dateComponents = DateComponents(calendar: calendar, era: era, year: year, month: month, day: day)
    }
    public init(calendar: Calendar = Calendar.current, epochDay: Int) {
        dateComponents = DateComponents(calendar: calendar, era: 1, year: 1970, month: 1, day: 1) + DateComponents(day: epochDay)
    }
    public func with(
        dayOfWeek: Int
    ) -> Self {
        let newDate = calendar.date(bySetting: .weekday, value: (dayOfWeek) % 7 + 1, of: calendar.date(from: self.dateComponents)!)!
        return Self(from: newDate)
    }
    public func with(
        calendar: Calendar? = nil,
        era: Int? = nil,
        year: Int? = nil,
        month: Int? = nil,
        day: Int? = nil
    ) -> Self {
        Self(
            calendar: calendar ?? self.calendar,
            era: era ?? self.era,
            year: year ?? self.year,
            month: month ?? self.month,
            day: day ?? self.day
        )
    }
    private init(_ components: DateComponents) {
        self.dateComponents = components
    }
    public func plus(era: Int) -> Self { Self(dateComponents + DateComponents(era: era)) }
    public func plus(year: Int) -> Self { Self(dateComponents + DateComponents(year: year)) }
    public func plus(month: Int) -> Self { Self(dateComponents + DateComponents(month: month)) }
    public func plus(day: Int) -> Self { Self(dateComponents + DateComponents(day: day)) }
    
    public func hash(into hasher: inout Hasher){
        hasher.combine(day)
        hasher.combine(month)
        hasher.combine(year)
        hasher.combine(era)
        hasher.combine(calendar)
    }
    
    public static func == (lhs: Self, rhs: Self) -> Bool {
        return lhs.day == rhs.day &&
            lhs.month == rhs.month &&
            lhs.year == rhs.year &&
            lhs.era == rhs.era &&
            lhs.calendar == rhs.calendar
    }
    
    public func truncatedToMonth() -> Self { Self(calendar: calendar, era: era, year: year, month: month, day: 1) }
    public func truncatedToYear() -> Self { Self(calendar: calendar, era: era, year: year, month: 1, day: 1) }
}
public struct LocalTime: HasDateComponents, Codable, Hashable {
    public init(from decoder: Decoder) throws {
        let container = try decoder.singleValueContainer()
        let temp = try container.decode(String.self)
        let str = temp[0] == "T" ? String(temp.dropFirst(1)) : temp
        let parts = str.split(separator: ":")
        guard let hour = Int(parts[0]) else { throw DecodingError.dataCorrupted(DecodingError.Context(codingPath: decoder.codingPath, debugDescription: "Hour missing")) }
        guard let minute = Int(parts[1]) else { throw DecodingError.dataCorrupted(DecodingError.Context(codingPath: decoder.codingPath, debugDescription: "Month missing")) }
        guard let second = parts.count >= 2 ? Double(parts[2]) : 0 else { throw DecodingError.dataCorrupted(DecodingError.Context(codingPath: decoder.codingPath, debugDescription: "Dat missing")) }
        self = LocalTime(hour:hour, minute:minute, second:Int(second))
    }
    public func encode(to encoder: Encoder) throws {
        var container = encoder.singleValueContainer()
        try container.encode(String(format: "%02d:%02d:%02d", hour, minute, second))
    }
    public init(from: Date = Date()) {
        self.init(calendar: Calendar.current, from: from)
    }
    public var dateComponents: DateComponents
    public var calendar: Calendar { get { dateComponents.calendar! } set { dateComponents.calendar = newValue } }
    public var hour: Int { get { dateComponents.hour! } set { dateComponents.hour = newValue } }
    public var minute: Int { get { dateComponents.minute! } set { dateComponents.minute = newValue } }
    public var second: Int { get { dateComponents.second! } set { dateComponents.second = newValue } }
    public var nanosecond: Int { get { dateComponents.nanosecond! } set { dateComponents.nanosecond = newValue } }
    public var secondOfDay: Int {
        return ((hour * 60 + minute) * 60 + second)
    }
    public init(calendar: Calendar = Calendar.current, secondOfDay: Int) {
        self.dateComponents = DateComponents(
            calendar: calendar,
            hour: secondOfDay / 3600,
            minute: (secondOfDay / 60) % 60,
            second: secondOfDay % 60,
            nanosecond: 0
        )
    }
    
    public init(
        calendar: Calendar,
        from: Date = Date()
    ) {
        dateComponents = calendar.dateComponents([.hour, .minute, .second, .nanosecond], from: from)
        self.calendar = calendar
    }
    public init(
        calendar: Calendar = Calendar.current,
        hour: Int,
        minute: Int = 0,
        second: Int = 0,
        nanosecond: Int = 0
    ) {
        dateComponents = DateComponents(calendar: calendar, hour: hour, minute: minute, second: second, nanosecond: nanosecond)
    }
    public func with(
        calendar: Calendar? = nil,
        hour: Int? = nil,
        minute: Int? = nil,
        second: Int? = nil,
        nanosecond: Int? = nil
    ) -> Self {
        Self(
            calendar: calendar ?? self.calendar,
            hour: hour ?? self.hour,
            minute: minute ?? self.minute,
            second: second ?? self.second,
            nanosecond: nanosecond ?? self.nanosecond
        )
    }
    private init(_ components: DateComponents) {
        self.dateComponents = components
    }
    public func plus(hour: Int) -> Self { Self(dateComponents + DateComponents(hour: hour)) }
    public func plus(minute: Int) -> Self { Self(dateComponents + DateComponents(minute: minute)) }
    public func plus(second: Int) -> Self { Self(dateComponents + DateComponents(second: second)) }
    public func plus(nanosecond: Int) -> Self { Self(dateComponents + DateComponents(nanosecond: nanosecond)) }
    public static func +(lhs: LocalTime, rhs: TimeInterval) -> LocalTime {
        return lhs.plus(second: Int(rhs))
    }
    public static func -(lhs: LocalTime, rhs: TimeInterval) -> LocalTime {
        return lhs.plus(second: -Int(rhs))
    }
    
    public static let MIN = LocalTime(hour: 0, minute: 0, second: 0, nanosecond: 0)
    public static let MAX = LocalTime(hour: 23, minute: 59, second: 59, nanosecond: 999_999_999)
    

    public func hash(into hasher: inout Hasher){
        hasher.combine(nanosecond)
        hasher.combine(second)
        hasher.combine(minute)
        hasher.combine(hour)
        hasher.combine(calendar)
    }
    
    public static func == (lhs: Self, rhs: Self) -> Bool {
        return lhs.nanosecond == rhs.nanosecond &&
            lhs.second == rhs.second &&
            lhs.minute == rhs.minute &&
            lhs.hour == rhs.hour &&
            lhs.calendar == rhs.calendar
    }
    
    public func truncatedToHour() -> Self { Self(calendar: calendar, hour: hour, minute: 0, second: 0, nanosecond: 0) }
    public func truncatedToMinute() -> Self { Self(calendar: calendar, hour: hour, minute: minute, second: 0, nanosecond: 0) }
}
public struct LocalDateTime: HasDateComponentsWithDay, Codable, Hashable {
    
    public init(from decoder: Decoder) throws {
        let container = try decoder.singleValueContainer()
        let str = try container.decode(String.self)
        let majorParts = str.split(separator: "T")
        let date = majorParts[0]
        let time =  majorParts[1]
        let timeParts = time.split(separator: ":")
        let dateParts = date.split(separator: "-")
        guard let year = Int(dateParts[0]) else { throw DecodingError.dataCorrupted(DecodingError.Context(codingPath: decoder.codingPath, debugDescription: "Year missing")) }
        guard let month = Int(dateParts[1]) else { throw DecodingError.dataCorrupted(DecodingError.Context(codingPath: decoder.codingPath, debugDescription: "Month missing")) }
        guard let day = Int(dateParts[2]) else { throw DecodingError.dataCorrupted(DecodingError.Context(codingPath: decoder.codingPath, debugDescription: "Day missing")) }
        guard let hour = Int(timeParts[0]) else { throw DecodingError.dataCorrupted(DecodingError.Context(codingPath: decoder.codingPath, debugDescription: "Hour missing")) }
        guard let minute = Int(timeParts[1]) else { throw DecodingError.dataCorrupted(DecodingError.Context(codingPath: decoder.codingPath, debugDescription: "Month missing")) }
        guard let second = timeParts.count >= 2 ? Double(timeParts[2]) : 0 else { throw DecodingError.dataCorrupted(DecodingError.Context(codingPath: decoder.codingPath, debugDescription: "Dat missing")) }
        self = LocalDateTime(year:year, month:month, day:day, hour:hour, minute:minute, second:Int(second))
    }
    public func encode(to encoder: Encoder) throws {
        var container = encoder.singleValueContainer()
        try container.encode(String(format: "%04d-%02d-%02dT%04d:%02d:%02d", year, month, day, hour, minute, second))
    }
    
    public init(from: Date = Date()) {
        self.init(calendar: Calendar.current, from: from)
    }
    public var dateComponents: DateComponents
    public var calendar: Calendar { get { dateComponents.calendar! } set { dateComponents.calendar = newValue } }
    public var era: Int { get { dateComponents.era! } set { dateComponents.era = newValue } }
    public var year: Int { get { dateComponents.year! } set { dateComponents.year = newValue } }
    public var month: Int { get { dateComponents.month! } set { dateComponents.month = newValue } }
    public var day: Int { get { dateComponents.day! } set { dateComponents.day = newValue } }
    public var hour: Int { get { dateComponents.hour! } set { dateComponents.hour = newValue } }
    public var minute: Int { get { dateComponents.minute! } set { dateComponents.minute = newValue } }
    public var second: Int { get { dateComponents.second! } set { dateComponents.second = newValue } }
    public var nanosecond: Int { get { dateComponents.nanosecond! } set { dateComponents.nanosecond = newValue } }
    public init(
        calendar: Calendar,
        from: Date = Date()
    ) {
        dateComponents = calendar.dateComponents([.era, .year, .month, .day, .hour, .minute, .second, .nanosecond], from: from)
        self.calendar = calendar
    }
    public func toDate() -> Date {
        return dateComponents.date!
    }
    public init(
        calendar: Calendar = Calendar.current,
        era: Int = 1,
        year: Int,
        month: Int,
        day: Int = 0,
        hour: Int = 0,
        minute: Int = 0,
        second: Int = 0,
        nanosecond: Int = 0
    ) {
        dateComponents = DateComponents(calendar: calendar, year: year, month: month, day: day, hour: hour, minute: minute, second: second, nanosecond: nanosecond)
    }
    public init(
        localDate: LocalDate,
        localTime: LocalTime
    ) {
        dateComponents = localDate.dateComponents
        absorb(other: localTime.dateComponents)
    }
    func toLocalDate() -> LocalDate { LocalDate(calendar: calendar, era: era, year: year, month: month, day: day) }
    func toLocalTime() -> LocalTime { LocalTime(calendar: calendar, hour: hour, minute: minute, second: second, nanosecond: nanosecond) }
    public func with(
        calendar: Calendar? = nil,
        era: Int? = nil,
        year: Int? = nil,
        month: Int? = nil,
        day: Int? = nil,
        hour: Int? = nil,
        minute: Int? = nil,
        second: Int? = nil,
        nanosecond: Int? = nil
    ) -> Self {
        Self(
            calendar: calendar ?? self.calendar,
            era: era ?? self.era,
            year: year ?? self.year,
            month: month ?? self.month,
            day: day ?? self.day,
            hour: hour ?? self.hour,
            minute: minute ?? self.minute,
            second: second ?? self.second,
            nanosecond: nanosecond ?? self.nanosecond
        )
    }
    public func with(
        _ localDate: LocalDate
    ) -> Self {
        with(
            calendar: localDate.calendar,
            era: localDate.era,
            year: localDate.year,
            month: localDate.month,
            day: localDate.day
        )
    }
    public func with(
        _ localTime: LocalTime
    ) -> Self {
        with(
            calendar: localTime.calendar,
            hour: localTime.hour,
            minute: localTime.minute,
            second: localTime.second,
            nanosecond: localTime.nanosecond
        )
    }
    private init(_ components: DateComponents) {
        self.dateComponents = components
    }
    public func plus(era: Int) -> Self { Self(dateComponents + DateComponents(era: era)) }
    public func plus(year: Int) -> Self { Self(dateComponents + DateComponents(year: year)) }
    public func plus(month: Int) -> Self { Self(dateComponents + DateComponents(month: month)) }
    public func plus(day: Int) -> Self { Self(dateComponents + DateComponents(day: day)) }
    public func plus(hour: Int) -> Self { Self(dateComponents + DateComponents(hour: hour)) }
    public func plus(minute: Int) -> Self { Self(dateComponents + DateComponents(minute: minute)) }
    public func plus(second: Int) -> Self { Self(dateComponents + DateComponents(second: second)) }
    public func plus(nanosecond: Int) -> Self { Self(dateComponents + DateComponents(nanosecond: nanosecond)) }
    

    public func hash(into hasher: inout Hasher){
        hasher.combine(day)
        hasher.combine(month)
        hasher.combine(year)
        hasher.combine(era)
        hasher.combine(second)
        hasher.combine(minute)
        hasher.combine(hour)
        hasher.combine(calendar)
    }
    
    public static func == (lhs: Self, rhs: Self) -> Bool {
        return lhs.calendar == rhs.calendar &&
            lhs.day == rhs.day &&
            lhs.month == rhs.month &&
            lhs.year == rhs.year &&
            lhs.era == rhs.era &&
            lhs.second == rhs.second &&
            lhs.minute == rhs.minute &&
            lhs.hour == rhs.hour &&
            lhs.day == rhs.day
    }
    
    public func truncatedToYear() -> Self { with(month: 1, day: 1, hour: 0, minute: 0, second: 0, nanosecond: 0) }
    public func truncatedToMonth() -> Self { with(day: 1, hour: 0, minute: 0, second: 0, nanosecond: 0) }
    public func truncatedToDay() -> Self { with(hour: 0, minute: 0, second: 0, nanosecond: 0) }
    public func truncatedToHour() -> Self { with(minute: 0, second: 0, nanosecond: 0) }
    public func truncatedToMinute() -> Self { with(second: 0, nanosecond: 0) }
    
    static public let MIN = LocalDateTime(localDate: LocalDate.MIN, localTime: LocalTime.MIN)
    static public let MAX = LocalDateTime(localDate: LocalDate.MAX, localTime: LocalTime.MAX)
}
public struct ZonedDateTime: HasDateComponentsWithDay, Codable, Hashable {
    
    public init(from decoder: Decoder) throws {
        let container = try decoder.singleValueContainer()
        let str = try container.decode(String.self)
        let majorParts = str.split(separator: "T")
        let date = majorParts[0]
        var time = String(majorParts[1])
        var timeZone: TimeZone? = nil
        if(time.contains("+")){
            let index = time.indexOf( "+")
            timeZone = TimeZone.init(iso8601:time.substring(index))
            time = time.substring(startIndex: 0, endIndex:index)
        } else if (time.contains("-")){
            let index = time.indexOf( "-")
            timeZone = TimeZone.init(iso8601:time.substring(index))
            time = time.substring(startIndex: 0, endIndex:index)
        } else if(time.contains("Z")){
            timeZone = TimeZone.init(iso8601: "Z")
            time = time.substring(startIndex: 0, endIndex:time.count - 1)
        }
        let dateParts = date.split(separator: "-")
        let timeParts = time.split(separator: ":")
        guard let year = Int(dateParts[0]) else { throw DecodingError.dataCorrupted(DecodingError.Context(codingPath: decoder.codingPath, debugDescription: "Year missing")) }
        guard let month = Int(dateParts[1]) else { throw DecodingError.dataCorrupted(DecodingError.Context(codingPath: decoder.codingPath, debugDescription: "Month missing")) }
        guard let day = Int(dateParts[2]) else { throw DecodingError.dataCorrupted(DecodingError.Context(codingPath: decoder.codingPath, debugDescription: "Day missing")) }
        guard let hour = Int(timeParts[0]) else { throw DecodingError.dataCorrupted(DecodingError.Context(codingPath: decoder.codingPath, debugDescription: "Hour missing")) }
        guard let minute = Int(timeParts[1]) else { throw DecodingError.dataCorrupted(DecodingError.Context(codingPath: decoder.codingPath, debugDescription: "Month missing")) }
        guard let second = timeParts.count >= 2 ? Double(timeParts[2]) : 0 else { throw DecodingError.dataCorrupted(DecodingError.Context(codingPath: decoder.codingPath, debugDescription: "Dat missing")) }
        self = ZonedDateTime(timeZone:timeZone ?? TimeZone.current, year:year, month:month, day:day, hour:hour, minute:minute, second:Int(second))
    }
    public func encode(to encoder: Encoder) throws {
        var container = encoder.singleValueContainer()
        let seconds = timeZone.secondsFromGMT()
        let hoursOffset = seconds / 3600
        let minuteOffset = abs(seconds / 60) % 60
        var output = "Z"
        if(seconds > 0){
            output = String(format: "+%02d:%02d", hoursOffset, minuteOffset)
        } else if(seconds < 0){
            output = String(format: "%02d:%02d", hoursOffset, minuteOffset)
        }
        try container.encode(String(format: "%04d-%02d-%02dT%02d:%02d:%02d%@", year, month, day, hour, minute, second, output))
    }

    public init(from: Date = Date()) {
        self.init(calendar: Calendar.current, from: from)
    }
    public var dateComponents: DateComponents
    public var calendar: Calendar { get { dateComponents.calendar! } set { dateComponents.calendar = newValue } }
    public var timeZone: TimeZone { get { dateComponents.timeZone! } set { dateComponents.timeZone = newValue } }
    public var era: Int { get { dateComponents.era! } set { dateComponents.era = newValue } }
    public var year: Int { get { dateComponents.year! } set { dateComponents.year = newValue } }
    public var month: Int { get { dateComponents.month! } set { dateComponents.month = newValue } }
    public var day: Int { get { dateComponents.day! } set { dateComponents.day = newValue } }
    public var hour: Int { get { dateComponents.hour! } set { dateComponents.hour = newValue } }
    public var minute: Int { get { dateComponents.minute! } set { dateComponents.minute = newValue } }
    public var second: Int { get { dateComponents.second! } set { dateComponents.second = newValue } }
    public var nanosecond: Int { get { dateComponents.nanosecond! } set { dateComponents.nanosecond = newValue } }
    public init(
        calendar: Calendar = Calendar.current,
        timeZone: TimeZone? = Calendar.current.timeZone,
        from: Date = Date()
    ) {
        dateComponents = calendar.dateComponents([.era, .year, .month, .day, .hour, .minute, .second, .nanosecond], from: from)
        self.timeZone = timeZone ?? calendar.timeZone
        self.calendar = calendar
    }
    public init(
        calendar: Calendar = Calendar.current,
        timeZone: TimeZone = Calendar.current.timeZone,
        era: Int = 1,
        year: Int,
        month: Int,
        day: Int,
        hour: Int = 0,
        minute: Int = 0,
        second: Int = 0,
        nanosecond: Int = 0
    ) {
        dateComponents = DateComponents(calendar: calendar, timeZone: timeZone, era: era, year: year, month: month, day: day, hour: hour, minute: minute, second: second, nanosecond: nanosecond)
    }
    public init(
        calendar: Calendar = Calendar.current,
        timeZone: TimeZone = Calendar.current.timeZone,
        localDateTime: LocalDateTime
    ) {
        dateComponents = DateComponents(calendar: calendar, timeZone: timeZone)
        absorb(other: localDateTime.dateComponents)
    }
    public init(
        calendar: Calendar = Calendar.current,
        timeZone: TimeZone = Calendar.current.timeZone,
        localDate: LocalDate,
        localTime: LocalTime
    ) {
        dateComponents = DateComponents(calendar: calendar, timeZone: timeZone)
        absorb(other: localDate.dateComponents)
        absorb(other: localTime.dateComponents)
    }
    public func toLocalDate() -> LocalDate { LocalDate(calendar: calendar, year: year, month: month, day: day) }
    public func toLocalTime() -> LocalTime { LocalTime(calendar: calendar, hour: hour, minute: minute, second: second, nanosecond: nanosecond) }
    public func toLocalDateTime() -> LocalDateTime { LocalDateTime(calendar: calendar, year: year, month: month, day: day, hour: hour, minute: minute, second: second, nanosecond: nanosecond) }
    public func toDate() -> Date {
        return self.calendar.date(from: self.dateComponents)!
    }
    public func with(
        calendar: Calendar? = nil,
        timeZone: TimeZone? = nil,
        era: Int? = nil,
        year: Int? = nil,
        month: Int? = nil,
        day: Int? = nil,
        hour: Int? = nil,
        minute: Int? = nil,
        second: Int? = nil,
        nanosecond: Int? = nil
    ) -> Self {
        Self(
            calendar: calendar ?? self.calendar,
            timeZone: timeZone ?? calendar?.timeZone ?? self.timeZone,
            era: era ?? self.era,
            year: year ?? self.year,
            month: month ?? self.month,
            day: day ?? self.day,
            hour: hour ?? self.hour,
            minute: minute ?? self.minute,
            second: second ?? self.second,
            nanosecond: nanosecond ?? self.nanosecond
        )
    }
    public func with(
        _ localDate: LocalDate
    ) -> Self {
        with(
            era: localDate.era,
            year: localDate.year,
            month: localDate.month,
            day: localDate.day
        )
    }
    public func with(
        _ localTime: LocalTime
    ) -> Self {
        with(
            hour: localTime.hour,
            minute: localTime.minute,
            second: localTime.second,
            nanosecond: localTime.nanosecond
        )
    }
    public func with(
        _ localDate: LocalDate,
        _ localTime: LocalTime
    ) -> Self {
        with(
            era: localDate.era,
            year: localDate.year,
            month: localDate.month,
            day: localDate.day,
            hour: localTime.hour,
            minute: localTime.minute,
            second: localTime.second,
            nanosecond: localTime.nanosecond
        )
    }
    public func with(
        _ localDateTime: LocalDateTime
    ) -> Self {
        with(
            era: localDateTime.era,
            year: localDateTime.year,
            month: localDateTime.month,
            day: localDateTime.day,
            hour: localDateTime.hour,
            minute: localDateTime.minute,
            second: localDateTime.second,
            nanosecond: localDateTime.nanosecond
        )
    }
    private init(_ components: DateComponents) {
        self.dateComponents = components
    }
    public func plus(era: Int) -> Self { Self(dateComponents + DateComponents(era: era)) }
    public func plus(year: Int) -> Self { Self(dateComponents + DateComponents(year: year)) }
    public func plus(month: Int) -> Self { Self(dateComponents + DateComponents(month: month)) }
    public func plus(day: Int) -> Self { Self(dateComponents + DateComponents(day: day)) }
    public func plus(hour: Int) -> Self { Self(dateComponents + DateComponents(hour: hour)) }
    public func plus(minute: Int) -> Self { Self(dateComponents + DateComponents(minute: minute)) }
    public func plus(second: Int) -> Self { Self(dateComponents + DateComponents(second: second)) }
    public func plus(nanosecond: Int) -> Self { Self(dateComponents + DateComponents(nanosecond: nanosecond)) }
    
    public func hash(into hasher: inout Hasher){
        hasher.combine(day)
        hasher.combine(month)
        hasher.combine(year)
        hasher.combine(era)
        hasher.combine(second)
        hasher.combine(minute)
        hasher.combine(hour)
        hasher.combine(calendar)
        hasher.combine(timeZone)
    }
    
    public static func == (lhs: Self, rhs: Self) -> Bool {
        return lhs.calendar == rhs.calendar &&
            lhs.day == rhs.day &&
            lhs.month == rhs.month &&
            lhs.year == rhs.year &&
            lhs.era == rhs.era &&
            lhs.second == rhs.second &&
            lhs.minute == rhs.minute &&
            lhs.hour == rhs.hour &&
            lhs.day == rhs.day &&
            lhs.timeZone == rhs.timeZone
    }
    
    public func truncatedToYear() -> Self { with(month: 1, day: 1, hour: 0, minute: 0, second: 0, nanosecond: 0) }
    public func truncatedToMonth() -> Self { with(day: 1, hour: 0, minute: 0, second: 0, nanosecond: 0) }
    public func truncatedToDay() -> Self { with(hour: 0, minute: 0, second: 0, nanosecond: 0) }
    public func truncatedToHour() -> Self { with(minute: 0, second: 0, nanosecond: 0) }
    public func truncatedToMinute() -> Self { with(second: 0, nanosecond: 0) }
    
    static public let MIN = ZonedDateTime(localDate: LocalDate.MIN, localTime: LocalTime.MIN)
    static public let MAX = ZonedDateTime(localDate: LocalDate.MAX, localTime: LocalTime.MAX)
}

public extension Date {
    func atZone(_ timeZone: TimeZone = TimeZone.current) -> ZonedDateTime {
        return ZonedDateTime(calendar: Calendar.current, timeZone: timeZone, from: self)
    }
    
}

public extension DateFormatter {
    convenience init(bothStyles: DateFormatter.Style = .none) {
        self.init()
        self.dateStyle = bothStyles
        self.timeStyle = bothStyles
        self.locale = Locale.current
    }
    convenience init(dateStyle: DateFormatter.Style = .none, timeStyle: DateFormatter.Style = .none) {
        self.init()
        self.dateStyle = dateStyle
        self.timeStyle = timeStyle
        self.locale = Locale.current
    }
    
    func withZone(_ zone: TimeZone) -> DateFormatter {
        self.timeZone = zone
        return self
    }
}

public extension Date {
    func format(_ formatter: DateFormatter) -> String { return formatter.string(from: self) }
}


extension TimeZone{
    init?(iso8601:String){
        guard iso8601.count > 0 else { return nil }
        
        if(iso8601 == "Z"){
            self.init(secondsFromGMT: 0)
            return
        }
        
        let sign =  iso8601[0]
        guard sign == "-" || sign == "+" else { return nil }
        var direction = 1
        if (sign == "-") { direction = -1 }
        
        let timePart = iso8601.substring(1)
        if(timePart.count == 4){
            guard let hours = Int(timePart.substring(0, 2)) else {return nil}
            guard let minutes = Int(timePart.substring(2, 5)) else {return nil}
            self.init(secondsFromGMT: direction * (hours * 3600 + minutes * 60))
            return
        }else{
            let parts = timePart.split(separator:":")
            guard let hours = Int(parts[0]) else { return nil }
            let minutes = Int(parts.getOrNull(index: 1) ?? "0") ?? 0
            self.init(secondsFromGMT: direction * (hours * 3600 + minutes * 60))
            return
        }
    }
}

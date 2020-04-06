//Stub file made with Khrysalis 2 (by Lightning Kite)
import Foundation

public extension Date {

    //--- Date.iso8601()
    func iso8601() -> String {
        let formatter = DateFormatter()
        formatter.locale = Locale(identifier: "en_US_POSIX")
        formatter.dateFormat = "yyyy-MM-dd'T'HH:mm:ssZZZZZ"
        return formatter.string(from: self)
    }

    init(_ milliseconds:Int64) {
        self = Date(timeIntervalSince1970: TimeInterval(milliseconds) / 1000)
    }
    
    //--- Date.time
    var time: Int64 {
        get {
            return timeIntervalSince1970.milliseconds
        }
        set(value) {
            self = Date(value)
        }
    }

    func before(_ other: Date) -> Bool {
        return self.time < other.time
    }
    func after(_ other: Date) -> Bool {
        return self.time > other.time
    }
    
    //--- Date.dayOfWeek
    //--- Date.dayOfMonth
    //--- Date.monthOfYear
    //--- Date.yearAd
    //--- Date.hourOfDay
    //--- Date.minuteOfHour
    //--- Date.secondOfMinute
    var dayOfWeek: Int32 { return Int32(Calendar.current.component(.weekday, from: self)) }
    var dayOfMonth: Int32 { return Int32(Calendar.current.component(.day, from: self)) }
    var monthOfYear: Int32 { return Int32(Calendar.current.component(.month, from: self)) }
    var yearAd: Int32 { return Int32(Calendar.current.component(.year, from: self)) }
    var hourOfDay: Int32 { return Int32(Calendar.current.component(.hour, from: self)) }
    var minuteOfHour: Int32 { return Int32(Calendar.current.component(.minute, from: self)) }
    var secondOfMinute: Int32 { return Int32(Calendar.current.component(.second, from: self)) }
    
    //--- Date.dateAlone
    var dateAlone: DateAlone {
        return DateAlone(yearAd, monthOfYear, dayOfMonth)
    }
    //--- Date.timeAlone
    var timeAlone: TimeAlone {
        return TimeAlone(hourOfDay, minuteOfHour, secondOfMinute)
    }

    //--- Date.sameDay(Date)
    //--- Date.sameMonth(Date)
    //--- Date.sameYear(Date)
    func sameDay(_ other: Date) -> Bool {
        return self.yearAd == other.yearAd && self.monthOfYear == other.monthOfYear && self.dayOfMonth == other.dayOfMonth
    }
    func sameMonth(_ other: Date) -> Bool {
        return self.yearAd == other.yearAd && self.monthOfYear == other.monthOfYear
    }
    func sameYear(_ other: Date) -> Bool {
        return self.yearAd == other.yearAd
    }

    //--- Date.dayOfWeek(Int, Date)
    //--- Date.dayOfMonth(Int, Date)
    //--- Date.monthOfYear(Int, Date)
    //--- Date.yearAd(Int, Date)
    //--- Date.hourOfDay(Int, Date)
    //--- Date.minuteOfHour(Int, Date)
    //--- Date.secondOfMinute(Int, Date)
    func dayOfWeek(_ value: Int32, _ existing: Date? = nil) -> Date {
        let components = Calendar.current.dateComponents([.weekday], from: self)
        let diff = Int(value) - components.weekday!
        return self + diff.days()
    }
    func dayOfMonth(_ value: Int32, _ existing: Date? = nil) -> Date {
        var components = Calendar.current.dateComponents([.year, .month, .hour, .minute, .second, .nanosecond], from: self)
        components.day = Int(value)
        return Calendar.current.date(from: components)!
    }
    func monthOfYear(_ value: Int32, _ existing: Date? = nil) -> Date {
        var components = Calendar.current.dateComponents([.year, .day, .hour, .minute, .second, .nanosecond], from: self)
        components.month = Int(value)
        return Calendar.current.date(from: components)!
    }
    func yearAd(_ value: Int32, _ existing: Date? = nil) -> Date {
        var components = Calendar.current.dateComponents([.month, .day, .hour, .minute, .second, .nanosecond], from: self)
        components.year = Int(value)
        return Calendar.current.date(from: components)!
    }
    func hourOfDay(_ value: Int32, _ existing: Date? = nil) -> Date {
        var components = Calendar.current.dateComponents([.year, .month, .day, .minute, .second, .nanosecond], from: self)
        components.hour = Int(value)
        return Calendar.current.date(from: components)!
    }
    func minuteOfHour(_ value: Int32, _ existing: Date? = nil) -> Date {
        var components = Calendar.current.dateComponents([.year, .month, .day, .hour, .second, .nanosecond], from: self)
        components.minute = Int(value)
        return Calendar.current.date(from: components)!
    }
    func secondOfMinute(_ value: Int32, _ existing: Date? = nil) -> Date {
        var components = Calendar.current.dateComponents([.year, .month, .day, .hour, .minute, .nanosecond], from: self)
        components.second = Int(value)
        return Calendar.current.date(from: components)!
    }
    
    //--- Date.addDayOfWeek(Int, Date)
    //--- Date.addDayOfMonth(Int, Date)
    //--- Date.addMonthOfYear(Int, Date)
    //--- Date.addYearAd(Int, Date)
    //--- Date.addHourOfDay(Int, Date)
    //--- Date.addMinuteOfHour(Int, Date)
    //--- Date.addSecondOfMinute(Int, Date)
    func addDayOfWeek(_ value: Int32, _ existing: Date? = nil) -> Date {
        return Calendar.current.date(byAdding: .weekday, value: Int(value), to: self)!
    }
    func addDayOfMonth(_ value: Int32, _ existing: Date? = nil) -> Date {
        return Calendar.current.date(byAdding: .day, value: Int(value), to: self)!
    }
    func addMonthOfYear(_ value: Int32, _ existing: Date? = nil) -> Date {
        return Calendar.current.date(byAdding: .month, value: Int(value), to: self)!
    }
    func addYearAd(_ value: Int32, _ existing: Date? = nil) -> Date {
        return Calendar.current.date(byAdding: .year, value: Int(value), to: self)!
    }
    func addHourOfDay(_ value: Int32, _ existing: Date? = nil) -> Date {
        return Calendar.current.date(byAdding: .hour, value: Int(value), to: self)!
    }
    func addMinuteOfHour(_ value: Int32, _ existing: Date? = nil) -> Date {
        return Calendar.current.date(byAdding: .minute, value: Int(value), to: self)!
    }
    func addSecondOfMinute(_ value: Int32, _ existing: Date? = nil) -> Date {
        return Calendar.current.date(byAdding: .second, value: Int(value), to: self)!
    }

    //--- Date.setDayOfWeek(Int)
    //--- Date.setDayOfMonth(Int)
    //--- Date.setMonthOfYear(Int)
    //--- Date.setYearAd(Int)
    //--- Date.setHourOfDay(Int)
    //--- Date.setMinuteOfHour(Int)
    //--- Date.setSecondOfMinute(Int)
    mutating func setDayOfWeek(_ value: Int32) -> Date { self = self.dayOfWeek(value); return self }
    mutating func setDayOfMonth(_ value: Int32) -> Date { self = self.dayOfMonth(value); return self }
    mutating func setMonthOfYear(_ value: Int32) -> Date { self = self.monthOfYear(value); return self }
    mutating func setYearAd(_ value: Int32) -> Date { self = self.yearAd(value); return self }
    mutating func setHourOfDay(_ value: Int32) -> Date { self = self.hourOfDay(value); return self }
    mutating func setMinuteOfHour(_ value: Int32) -> Date { self = self.minuteOfHour(value); return self }
    mutating func setSecondOfMinute(_ value: Int32) -> Date { self = self.secondOfMinute(value); return self }

    //--- Date.setAddDayOfWeek(Int)
    //--- Date.setAddDayOfMonth(Int)
    //--- Date.setAddMonthOfYear(Int)
    //--- Date.setAddYearAd(Int)
    //--- Date.setAddHourOfDay(Int)
    //--- Date.setAddMinuteOfHour(Int)
    //--- Date.setAddSecondOfMinute(Int)
    mutating func setAddDayOfWeek(_ value: Int32) -> Date { self = self.addDayOfWeek(value); return self }
    mutating func setAddDayOfMonth(_ value: Int32) -> Date { self = self.addDayOfMonth(value); return self }
    mutating func setAddMonthOfYear(_ value: Int32) -> Date { self = self.addMonthOfYear(value); return self }
    mutating func setAddYearAd(_ value: Int32) -> Date { self = self.addYearAd(value); return self }
    mutating func setAddHourOfDay(_ value: Int32) -> Date { self = self.addHourOfDay(value); return self }
    mutating func setAddMinuteOfHour(_ value: Int32) -> Date { self = self.addMinuteOfHour(value); return self }
    mutating func setAddSecondOfMinute(_ value: Int32) -> Date { self = self.addSecondOfMinute(value); return self }

    //--- Date.set(DateAlone)
    mutating func set(_ dateAlone: DateAlone) -> Date {
        var components = Calendar.current.dateComponents([.hour, .minute, .second, .nanosecond], from: self)
        components.year = Int(dateAlone.year)
        components.month = Int(dateAlone.month)
        components.day = Int(dateAlone.day)
        self = Calendar.current.date(from: components)!
        return self
    }
    mutating func set(dateAlone: DateAlone) -> Date {
        set(dateAlone)
    }
    
    //--- Date.set(TimeAlone)
    mutating func set(_ timeAlone: TimeAlone) -> Date {
        var components = Calendar.current.dateComponents([.year, .month, .day], from: self)
        components.hour = Int(timeAlone.hour)
        components.minute = Int(timeAlone.minute)
        components.second = Int(timeAlone.second)
        self = Calendar.current.date(from: components)!
        return self
    }
    mutating func set(timeAlone: TimeAlone) -> Date {
        set(timeAlone)
    }
    
    //--- Date.set(DateAlone, TimeAlone)
    mutating func set(_ dateAlone: DateAlone, _ timeAlone: TimeAlone) -> Date {
        var components = Calendar.current.dateComponents([], from: self)
        components.year = Int(dateAlone.year)
        components.month = Int(dateAlone.month)
        components.day = Int(dateAlone.day)
        components.hour = Int(timeAlone.hour)
        components.minute = Int(timeAlone.minute)
        components.second = Int(timeAlone.second)
        self = Calendar.current.date(from: components)!
        return self
    }
    mutating func set(dateAlone: DateAlone, timeAlone: TimeAlone) -> Date {
        set(dateAlone, timeAlone)
    }

    //--- Date.format(ClockPartSize, ClockPartSize)
    func format(_ dateStyle: ClockPartSize, _ timeStyle: ClockPartSize) -> String {
        return format(dateStyle: dateStyle, timeStyle: timeStyle)
    }
    func format(dateStyle: ClockPartSize, timeStyle: ClockPartSize) -> String {
        var rawDateStyle: DateFormatter.Style = .none
        switch dateStyle {
            case .None: rawDateStyle = .none
            case .Short: rawDateStyle = .short
            case .Medium: rawDateStyle = .medium
            case .Long: rawDateStyle = .long
            case .Full: rawDateStyle = .full
        }
        var rawTimeStyle: DateFormatter.Style = .none
        switch timeStyle {
            case .None: rawTimeStyle = .none
            case .Short: rawTimeStyle = .short
            case .Medium: rawTimeStyle = .medium
            case .Long: rawTimeStyle = .long
            case .Full: rawTimeStyle = .full
        }
        
        let formatter = DateFormatter()
        formatter.dateStyle = rawDateStyle
        formatter.timeStyle = rawTimeStyle
        return formatter.string(from: self)
    }
    

    //--- Date.minus(TimeInterval)

    //--- Date.plus(TimeInterval)
}

//--- DateAlone.set(Date)
public extension DateAlone {
    func set(_ date: Date) -> DateAlone {
        let components = Calendar.current.dateComponents([.year, .month, .day], from: date)
        self.year = Int32(components.year!)
        self.month = Int32(components.month!)
        self.day = Int32(components.day!)
        return self
    }
}

//--- TimeAlone.set(Date)
public extension TimeAlone {
    func set(_ date: Date) -> TimeAlone {
        let components = Calendar.current.dateComponents([.hour, .minute, .second], from: date)
        self.hour = Int32(components.hour!)
        self.minute = Int32(components.minute!)
        self.second = Int32(components.second!)
        return self
    }
}

//--- dateFrom(DateAlone, TimeAlone, Date)
public func dateFrom(_ dateAlone: DateAlone, _ timeAlone: TimeAlone) -> Date {
    return dateFrom(dateAlone: dateAlone, timeAlone: timeAlone)
}
public func dateFrom(dateAlone: DateAlone, timeAlone: TimeAlone) -> Date {
    let components = DateComponents(
        year: Int(dateAlone.year),
        month: Int(dateAlone.month),
        day: Int(dateAlone.day),
        hour: Int(timeAlone.hour),
        minute: Int(timeAlone.minute),
        second: Int(timeAlone.second),
        nanosecond: 0
    )
    return Calendar.current.date(from: components)!
}

//--- dateFromIso(String)
public func dateFromIso(_ iso8601: String) -> Date? {
    ISO8601DateFormatter().date(from: iso8601)
}
public func dateFromIso(iso8601: String) -> Date? {
    return dateFromIso(iso8601)
}

//Stub file made with Kwift 2 (by Lightning Kite)
import Foundation

//--- DateAlone.{
//--- DateAlone.Primary Constructor
//--- DateAlone.Companion.{
//--- DateAlone.Companion.now()
//--- DateAlone.Companion.farPast
//--- DateAlone.Companion.farFuture
//--- DateAlone.Companion.iso(String)
//--- DateAlone.Companion.fromMonthInEra(Int)
//--- DateAlone.Companion.}
//--- DateAlone.monthInEra
//--- DateAlone.comparable
//--- DateAlone.dayOfWeek
//--- DateAlone.}
public class DateAlone: Equatable, Hashable, Codable {
    
    required public init(from decoder: Decoder) throws {
        let string: String = try decoder.singleValueContainer().decode(String.self)
        year = string.substringBefore("-").toInt()
        month = string.substringAfter("-").substringBefore("-").toInt()
        day = string.substringAfterLast("-").toInt()
    }
    
    public func encode(to encoder: Encoder) throws {
        var container = encoder.singleValueContainer()
        try container.encode(iso8601())
    }
    
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
    
    static public func fromMonthInEra(monthInEra: Int32) -> DateAlone {
        return DateAlone(year: ( monthInEra - 1 ) / 12, month: ( monthInEra - 1 ) % 12 + 1, day: 1)
    }
    static public func fromMonthInEra(_ monthInEra: Int32) -> DateAlone {
        return fromMonthInEra(monthInEra: monthInEra)
    }
    //End Companion
    
    public var monthInEra: Int32 {
        get {
            return self.year * 12 + self.month
        }
    }
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
    
    public init(year: Int32, month: Int32, day: Int32) {
        self.year = year
        self.month = month
        self.day = day
    }
    convenience public init(_ year: Int32, _ month: Int32, _ day: Int32) {
        self.init(year: year, month: month, day: day)
    }
}

public extension DateAlone {
    //--- DateAlone.iso8601()
    func iso8601() -> String {
        let formatter = DateFormatter()
        formatter.locale = Locale(identifier: "en_US_POSIX")
        formatter.dateFormat = "yyyy-MM-dd"
        return formatter.string(from: dateFrom(self, TimeAlone.noon))
    }

    //--- DateAlone.setDayOfMonth(Int)
    //--- DateAlone.setMonthOfYear(Int)
    //--- DateAlone.setYearAd(Int)
    //--- DateAlone.setDayOfWeek(Int)
    func setDayOfWeek(_ value: Int32) -> DateAlone { return set(dateFrom(self, TimeAlone.noon).dayOfWeek(value)) }
    func setDayOfMonth(_ value: Int32) -> DateAlone { return set(dateFrom(self, TimeAlone.noon).dayOfMonth(value)) }
    func setMonthOfYear(_ value: Int32) -> DateAlone { return set(dateFrom(self, TimeAlone.noon).monthOfYear(value)) }
    func setYearAd(_ value: Int32) -> DateAlone { return set(dateFrom(self, TimeAlone.noon).yearAd(value)) }

    //--- DateAlone.setAddDayOfWeek(Int)
    //--- DateAlone.setAddDayOfMonth(Int)
    //--- DateAlone.setAddMonthOfYear(Int)
    //--- DateAlone.setAddYearAd(Int)
    func setAddDayOfWeek(_ value: Int32) -> DateAlone { return set(dateFrom(self, TimeAlone.noon).addDayOfWeek(value)) }
    func setAddDayOfMonth(_ value: Int32) -> DateAlone { return set(dateFrom(self, TimeAlone.noon).addDayOfMonth(value)) }
    func setAddMonthOfYear(_ value: Int32) -> DateAlone { return set(dateFrom(self, TimeAlone.noon).addMonthOfYear(value)) }
    func setAddYearAd(_ value: Int32) -> DateAlone { return set(dateFrom(self, TimeAlone.noon).addYearAd(value)) }

    //--- DateAlone.dayOfMonth(Int)
    //--- DateAlone.monthOfYear(Int)
    //--- DateAlone.yearAd(Int)
    //--- DateAlone.dayOfWeek(Int)
    func dayOfWeek(_ value: Int32) -> DateAlone { return dateFrom(self, TimeAlone.noon).dayOfWeek(value).dateAlone }
    func dayOfMonth(_ value: Int32) -> DateAlone { return dateFrom(self, TimeAlone.noon).dayOfMonth(value).dateAlone }
    func monthOfYear(_ value: Int32) -> DateAlone { return dateFrom(self, TimeAlone.noon).monthOfYear(value).dateAlone }
    func yearAd(_ value: Int32) -> DateAlone { return dateFrom(self, TimeAlone.noon).yearAd(value).dateAlone }

    //--- DateAlone.addDayOfWeek(Int)
    //--- DateAlone.addDayOfMonth(Int)
    //--- DateAlone.addMonthOfYear(Int)
    //--- DateAlone.addYearAd(Int)
    func addDayOfWeek(_ value: Int32) -> DateAlone { return dateFrom(self, TimeAlone.noon).addDayOfWeek(value).dateAlone }
    func addDayOfMonth(_ value: Int32) -> DateAlone { return dateFrom(self, TimeAlone.noon).addDayOfMonth(value).dateAlone }
    func addMonthOfYear(_ value: Int32) -> DateAlone { return dateFrom(self, TimeAlone.noon).addMonthOfYear(value).dateAlone }
    func addYearAd(_ value: Int32) -> DateAlone { return dateFrom(self, TimeAlone.noon).addYearAd(value).dateAlone }
}





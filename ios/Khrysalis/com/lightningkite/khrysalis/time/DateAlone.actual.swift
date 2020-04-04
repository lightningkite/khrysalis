//Stub file made with Khrysalis 2 (by Lightning Kite)
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

    static public func iso(string: String) -> DateAlone? {
        if let year = string.substringBefore("-", "").toIntOrNull(),
            let month = string.substringAfter("-", "").substringBefore("-", "").toIntOrNull(),
            let day = string.substringAfter("-", "").substringAfter("-", "").toIntOrNull()
        {
            return DateAlone(year, month, day)
        }
        return nil
    }
    static public func iso(_ string: String) -> DateAlone? {
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
            return (
                day
              + ((153 * (month + 12 * ((14 - month) / 12) - 3) + 2) / 5)
              + (365 * (year + 4800 - ((14 - month) / 12)))
              + ((year + 4800 - ((14 - month) / 12)) / 4)
              - ((year + 4800 - ((14 - month) / 12)) / 100)
              + ((year + 4800 - ((14 - month) / 12)) / 400)
              - 32045
              + 1
            ) % 7 + 1
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

    func toString() -> String {
        return "\(year)-\(month)-\(day)"
    }
}

public extension DateAlone {

    private static var monthDaysNormal: Array<Int32> = [31,28,31,30,31,30,31,31,30,31,30,31]
    private static var monthDaysLeap: Array<Int32> = [31,29,31,30,31,30,31,31,30,31,30,31]
    private static func isLeapYear(_ year: Int32) -> Bool {
        return year % 4 == 0 && (year % 100 != 0 || year % 400 == 0)
    }
    private static func daysForMonth(year: Int32, month: Int32) -> Int32 {
        if isLeapYear(year) {
            return monthDaysLeap[month-1]
        } else {
            return monthDaysNormal[month-1]
        }
    }
    private func addDays(_ days: Int32) {
        var newDays = self.day + days
        while newDays <= 0 {
            self.addMonths(-1)
            newDays += DateAlone.daysForMonth(year: year, month: month)
        }
        while newDays > DateAlone.daysForMonth(year: year, month: month) {
            newDays -= DateAlone.daysForMonth(year: year, month: month)
            self.addMonths(1)
        }
        self.day = newDays
    }
    private func addMonths(_ months: Int32){
        let yearsMod = (months + month - 1).floorDiv(12)
        month = (months + month - 1).floorMod(12) + 1
        year += yearsMod
    }
    private func cap(){
        day = day.coerceIn(1, DateAlone.daysForMonth(year: year, month: month))
        month = month.coerceIn(1, 12)
    }
    private func correct(){
        month = month.coerceIn(1, 12)
        var newDays = self.day
        while newDays <= 0 {
            self.addMonths(-1)
            newDays += DateAlone.daysForMonth(year: year, month: month)
        }
        while newDays > DateAlone.daysForMonth(year: year, month: month) {
            newDays -= DateAlone.daysForMonth(year: year, month: month)
            self.addMonths(1)
        }
        self.day = newDays
    }

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
    func setDayOfWeek(_ value: Int32) -> DateAlone {
        self.addDays(value - dayOfWeek)
        correct()
        return self
    }
    func setDayOfMonth(_ value: Int32) -> DateAlone {
        self.day = value
        correct()
        return self
    }
    func setMonthOfYear(_ value: Int32) -> DateAlone {
        self.month = value
        correct()
        return self
    }
    func setYearAd(_ value: Int32) -> DateAlone {
        self.year = value
        correct()
        return self
    }

    //--- DateAlone.setAddDayOfWeek(Int)
    //--- DateAlone.setAddDayOfMonth(Int)
    //--- DateAlone.setAddMonthOfYear(Int)
    //--- DateAlone.setAddYearAd(Int)
    func setAddDayOfWeek(_ value: Int32) -> DateAlone { addDays(value); return self }
    func setAddDayOfMonth(_ value: Int32) -> DateAlone { addDays(value); return self }
    func setAddMonthOfYear(_ value: Int32) -> DateAlone { addMonths(value); cap(); return self }
    func setAddYearAd(_ value: Int32) -> DateAlone { year += value; cap(); return self }

    //--- DateAlone.dayOfMonth(Int)
    //--- DateAlone.monthOfYear(Int)
    //--- DateAlone.yearAd(Int)
    //--- DateAlone.dayOfWeek(Int)
    func dayOfWeek(_ value: Int32) -> DateAlone {
        return copy().setDayOfWeek(value)
    }
    func dayOfMonth(_ value: Int32) -> DateAlone {
        return copy().setDayOfMonth(value)
    }
    func monthOfYear(_ value: Int32) -> DateAlone {
        return copy().setMonthOfYear(value)
    }
    func yearAd(_ value: Int32) -> DateAlone {
        return copy().setYearAd(value)
    }

    //--- DateAlone.addDayOfWeek(Int)
    //--- DateAlone.addDayOfMonth(Int)
    //--- DateAlone.addMonthOfYear(Int)
    //--- DateAlone.addYearAd(Int)
    func addDayOfWeek(_ value: Int32) -> DateAlone {
        return copy().setAddDayOfWeek(value)
    }
    func addDayOfMonth(_ value: Int32) -> DateAlone {
        return copy().setAddDayOfMonth(value)
    }
    func addMonthOfYear(_ value: Int32) -> DateAlone {
        return copy().setAddMonthOfYear(value)
    }
    func addYearAd(_ value: Int32) -> DateAlone {
        return copy().setAddYearAd(value)
    }

    //--- DateAlone.formatYearless(ClockPartSize)
    func formatYearless(_ partSize: ClockPartSize) -> String {
        var template = "EEEEdMMM"
        switch partSize {
        case .Full:
            template = "EEEEdMMMM"
        case .Long:
            template = "EEEEdMMM"
        case .Medium:
            template = "dMMMM"
        case .Short:
            template = "dMMM"
        case .None:
            return ""
        }
        let format = DateFormatter.dateFormat(fromTemplate: template, options: 0, locale: Locale.current)
        let formatter = DateFormatter()
        formatter.dateFormat = format
        return formatter.string(from: dateFrom(self, TimeAlone.noon))
    }
}

public extension DateAlone {
    var safeDayOfWeek: Int32 {
        get {
            return dateFrom(self, TimeAlone.noon).dayOfWeek
        }
    }

    //--- DateAlone.setDayOfMonth(Int)
    //--- DateAlone.setMonthOfYear(Int)
    //--- DateAlone.setYearAd(Int)
    //--- DateAlone.setDayOfWeek(Int)
    func safeSetDayOfWeek(_ value: Int32) -> DateAlone { return set(dateFrom(self, TimeAlone.noon).dayOfWeek(value)) }
    func safeSetDayOfMonth(_ value: Int32) -> DateAlone { return set(dateFrom(self, TimeAlone.noon).dayOfMonth(value)) }
    func safeSetMonthOfYear(_ value: Int32) -> DateAlone { return set(dateFrom(self, TimeAlone.noon).monthOfYear(value)) }
    func safeSetYearAd(_ value: Int32) -> DateAlone { return set(dateFrom(self, TimeAlone.noon).yearAd(value)) }

    //--- DateAlone.setAddDayOfWeek(Int)
    //--- DateAlone.setAddDayOfMonth(Int)
    //--- DateAlone.setAddMonthOfYear(Int)
    //--- DateAlone.setAddYearAd(Int)
    func safeSetAddDayOfWeek(_ value: Int32) -> DateAlone { return set(dateFrom(self, TimeAlone.noon).addDayOfWeek(value)) }
    func safeSetAddDayOfMonth(_ value: Int32) -> DateAlone { return set(dateFrom(self, TimeAlone.noon).addDayOfMonth(value)) }
    func safeSetAddMonthOfYear(_ value: Int32) -> DateAlone { return set(dateFrom(self, TimeAlone.noon).addMonthOfYear(value)) }
    func safeSetAddYearAd(_ value: Int32) -> DateAlone { return set(dateFrom(self, TimeAlone.noon).addYearAd(value)) }

    //--- DateAlone.dayOfMonth(Int)
    //--- DateAlone.monthOfYear(Int)
    //--- DateAlone.yearAd(Int)
    //--- DateAlone.dayOfWeek(Int)
    func safeDayOfWeek(_ value: Int32) -> DateAlone { return dateFrom(self, TimeAlone.noon).dayOfWeek(value).dateAlone }
    func safeDayOfMonth(_ value: Int32) -> DateAlone { return dateFrom(self, TimeAlone.noon).dayOfMonth(value).dateAlone }
    func safeMonthOfYear(_ value: Int32) -> DateAlone { return dateFrom(self, TimeAlone.noon).monthOfYear(value).dateAlone }
    func safeYearAd(_ value: Int32) -> DateAlone { return dateFrom(self, TimeAlone.noon).yearAd(value).dateAlone }

    //--- DateAlone.addDayOfWeek(Int)
    //--- DateAlone.addDayOfMonth(Int)
    //--- DateAlone.addMonthOfYear(Int)
    //--- DateAlone.addYearAd(Int)
    func safeAddDayOfWeek(_ value: Int32) -> DateAlone { return dateFrom(self, TimeAlone.noon).addDayOfWeek(value).dateAlone }
    func safeAddDayOfMonth(_ value: Int32) -> DateAlone { return dateFrom(self, TimeAlone.noon).addDayOfMonth(value).dateAlone }
    func safeAddMonthOfYear(_ value: Int32) -> DateAlone { return dateFrom(self, TimeAlone.noon).addMonthOfYear(value).dateAlone }
    func safeAddYearAd(_ value: Int32) -> DateAlone { return dateFrom(self, TimeAlone.noon).addYearAd(value).dateAlone }
}

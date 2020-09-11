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
public class DateAlone: Equatable, Hashable, Codable, KStringable {

    required public init(from decoder: Decoder) throws {
        let string: String = try decoder.singleValueContainer().decode(String.self)
        year = Int(string.substringBefore(delimiter: "-"))!
        month = Int(string.substringAfter(delimiter: "-").substringBefore(delimiter: "-"))!
        day = Int(string.substringAfterLast(delimiter: "-"))!
    }

    public func encode(to encoder: Encoder) throws {
        var container = encoder.singleValueContainer()
        try container.encode(iso8601())
    }

    public var year: Int
    public var month: Int
    public var day: Int

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
        year: (Int)? = nil,
        month: (Int)? = nil,
        day: (Int)? = nil
    ) -> DateAlone {
        return DateAlone(
            year: year ?? self.year,
            month: month ?? self.month,
            day: day ?? self.day
        )
    }


    //Start Companion

    public class Companion {
        static public let INSTANCE = Companion()

        public func now() -> DateAlone {
            return Date().dateAlone
        }
        public var farPast = DateAlone(-99999, 1, 1)
        public var farFuture = DateAlone(99999, 12, 31)

        public func iso(string: String) -> DateAlone? {
            if let year = Int(string.substringBefore(delimiter: "-", missingDelimiterValue: "")),
                let month = Int(string.substringAfter(delimiter: "-", missingDelimiterValue: "").substringBefore(delimiter: "-", missingDelimiterValue: "")),
                let day = Int(string.substringAfter(delimiter: "-", missingDelimiterValue: "").substringAfter(delimiter: "-", missingDelimiterValue: ""))
            {
                return DateAlone(year, month, day)
            }
            return nil
        }
        public func iso(_ string: String) -> DateAlone? {
            return iso(string: string)
        }

        public func fromMonthInEra(monthInEra: Int) -> DateAlone {
            return DateAlone(year: ( monthInEra - 1 ) / 12, month: ( monthInEra - 1 ) % 12 + 1, day: 1)
        }
        public func fromMonthInEra(_ monthInEra: Int) -> DateAlone {
            return fromMonthInEra(monthInEra: monthInEra)
        }
    }

    static public func now() -> DateAlone { return Companion.INSTANCE.now() }
    static public var farPast: DateAlone { return Companion.INSTANCE.farPast }
    static public var farFuture: DateAlone { return Companion.INSTANCE.farFuture }

    static public func iso(string: String) -> DateAlone? { return Companion.INSTANCE.iso(string: string) }
    static public func iso(_ string: String) -> DateAlone? { return iso(string: string) }

    static public func fromMonthInEra(monthInEra: Int) -> DateAlone { return Companion.INSTANCE.fromMonthInEra(monthInEra: monthInEra) }
    static public func fromMonthInEra(_ monthInEra: Int) -> DateAlone { return fromMonthInEra(monthInEra: monthInEra) }
    //End Companion

    public var monthInEra: Int {
        get {
            return self.year * 12 + self.month
        }
    }
    public var comparable: Int {
        get {
            return self.year * 12 * 31 + self.month * 31 + self.day
        }
    }
    public var dayOfWeek: Int {
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

    public init(year: Int, month: Int, day: Int) {
        self.year = year
        self.month = month
        self.day = day
    }
    convenience public init(_ year: Int, _ month: Int, _ day: Int) {
        self.init(year: year, month: month, day: day)
    }

    public func toString() -> String {
        return "\(year)-\(month)-\(day)"
    }
}

public extension DateAlone {

    private static var monthDaysNormal: Array<Int> = [31,28,31,30,31,30,31,31,30,31,30,31]
    private static var monthDaysLeap: Array<Int> = [31,29,31,30,31,30,31,31,30,31,30,31]
    private static func isLeapYear(_ year: Int) -> Bool {
        return year % 4 == 0 && (year % 100 != 0 || year % 400 == 0)
    }
    private static func daysForMonth(year: Int, month: Int) -> Int {
        if isLeapYear(year) {
            return monthDaysLeap[month-1]
        } else {
            return monthDaysNormal[month-1]
        }
    }
    private func addDays(_ days: Int) {
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
    private func addMonths(_ months: Int){
        let yearsMod = (months + month - 1).floorDiv(other: 12)
        month = (months + month - 1).floorMod(other: 12) + 1
        year += yearsMod
    }
    private func cap(){
        day = min(max(day, 1), DateAlone.daysForMonth(year: year, month: month))
        month = min(max(month, 1), 12)
    }
    private func correct(){
        month = min(max(month, 1), 12)
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
        return formatter.string(from: dateFrom(dateAlone: self, timeAlone: TimeAlone.noon))
    }

    //--- DateAlone.setDayOfMonth(Int)
    //--- DateAlone.setMonthOfYear(Int)
    //--- DateAlone.setYearAd(Int)
    //--- DateAlone.setDayOfWeek(Int)
    func setDayOfWeek(value: Int) -> DateAlone {
        self.addDays(value - dayOfWeek)
        correct()
        return self
    }
    func setDayOfMonth(value: Int) -> DateAlone {
        self.day = value
        correct()
        return self
    }
    func setMonthOfYear(value: Int) -> DateAlone {
        self.month = value
        correct()
        return self
    }
    func setYearAd(value: Int) -> DateAlone {
        self.year = value
        correct()
        return self
    }

    //--- DateAlone.setAddDayOfWeek(Int)
    //--- DateAlone.setAddDayOfMonth(Int)
    //--- DateAlone.setAddMonthOfYear(Int)
    //--- DateAlone.setAddYearAd(Int)
    func setAddDayOfWeek(value: Int) -> DateAlone { addDays(value); return self }
    func setAddDayOfMonth(value: Int) -> DateAlone { addDays(value); return self }
    func setAddMonthOfYear(value: Int) -> DateAlone { addMonths(value); cap(); return self }
    func setAddYearAd(value: Int) -> DateAlone { year += value; cap(); return self }

    //--- DateAlone.dayOfMonth(Int)
    //--- DateAlone.monthOfYear(Int)
    //--- DateAlone.yearAd(Int)
    //--- DateAlone.dayOfWeek(Int)
    func dayOfWeek(value: Int) -> DateAlone {
        return copy().setDayOfWeek(value: value)
    }
    func dayOfMonth(value: Int) -> DateAlone {
        return copy().setDayOfMonth(value: value)
    }
    func monthOfYear(value: Int) -> DateAlone {
        return copy().setMonthOfYear(value: value)
    }
    func yearAd(value: Int) -> DateAlone {
        return copy().setYearAd(value: value)
    }

    //--- DateAlone.addDayOfWeek(Int)
    //--- DateAlone.addDayOfMonth(Int)
    //--- DateAlone.addMonthOfYear(Int)
    //--- DateAlone.addYearAd(Int)
    func addDayOfWeek(value: Int) -> DateAlone {
        return copy().setAddDayOfWeek(value: value)
    }
    func addDayOfMonth(value: Int) -> DateAlone {
        return copy().setAddDayOfMonth(value: value)
    }
    func addMonthOfYear(value: Int) -> DateAlone {
        return copy().setAddMonthOfYear(value: value)
    }
    func addYearAd(value: Int) -> DateAlone {
        return copy().setAddYearAd(value: value)
    }

    //--- DateAlone.formatYearless(ClockPartSize)
    func formatYearless(partSize: ClockPartSize) -> String {
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
        return formatter.string(from: dateFrom(dateAlone: self, timeAlone: TimeAlone.noon))
    }
}

public extension DateAlone {
    var safeDayOfWeek: Int {
        get {
            return dateFrom(dateAlone: self, timeAlone: TimeAlone.noon).dayOfWeek
        }
    }

    //--- DateAlone.setDayOfMonth(Int)
    //--- DateAlone.setMonthOfYear(Int)
    //--- DateAlone.setYearAd(Int)
    //--- DateAlone.setDayOfWeek(Int)
    func safeSetDayOfWeek(value: Int) -> DateAlone { return set(date: dateFrom(dateAlone: self, timeAlone: TimeAlone.noon).dayOfWeek(value: value)) }
    func safeSetDayOfMonth(value: Int) -> DateAlone { return set(date: dateFrom(dateAlone: self, timeAlone: TimeAlone.noon).dayOfMonth(value: value)) }
    func safeSetMonthOfYear(value: Int) -> DateAlone { return set(date: dateFrom(dateAlone: self, timeAlone: TimeAlone.noon).monthOfYear(value: value)) }
    func safeSetYearAd(value: Int) -> DateAlone { return set(date: dateFrom(dateAlone: self, timeAlone: TimeAlone.noon).yearAd(value: value)) }

    //--- DateAlone.setAddDayOfWeek(Int)
    //--- DateAlone.setAddDayOfMonth(Int)
    //--- DateAlone.setAddMonthOfYear(Int)
    //--- DateAlone.setAddYearAd(Int)
    func safeSetAddDayOfWeek(value: Int) -> DateAlone { return set(date: dateFrom(dateAlone: self, timeAlone: TimeAlone.noon).addDayOfWeek(value: value)) }
    func safeSetAddDayOfMonth(value: Int) -> DateAlone { return set(date: dateFrom(dateAlone: self, timeAlone: TimeAlone.noon).addDayOfMonth(value: value)) }
    func safeSetAddMonthOfYear(value: Int) -> DateAlone { return set(date: dateFrom(dateAlone: self, timeAlone: TimeAlone.noon).addMonthOfYear(value: value)) }
    func safeSetAddYearAd(value: Int) -> DateAlone { return set(date: dateFrom(dateAlone: self, timeAlone: TimeAlone.noon).addYearAd(value: value)) }

    //--- DateAlone.dayOfMonth(Int)
    //--- DateAlone.monthOfYear(Int)
    //--- DateAlone.yearAd(Int)
    //--- DateAlone.dayOfWeek(Int)
    func safeDayOfWeek(value: Int) -> DateAlone { return dateFrom(dateAlone: self, timeAlone: TimeAlone.noon).dayOfWeek(value: value).dateAlone }
    func safeDayOfMonth(value: Int) -> DateAlone { return dateFrom(dateAlone: self, timeAlone: TimeAlone.noon).dayOfMonth(value: value).dateAlone }
    func safeMonthOfYear(value: Int) -> DateAlone { return dateFrom(dateAlone: self, timeAlone: TimeAlone.noon).monthOfYear(value: value).dateAlone }
    func safeYearAd(value: Int) -> DateAlone { return dateFrom(dateAlone: self, timeAlone: TimeAlone.noon).yearAd(value: value).dateAlone }

    //--- DateAlone.addDayOfWeek(Int)
    //--- DateAlone.addDayOfMonth(Int)
    //--- DateAlone.addMonthOfYear(Int)
    //--- DateAlone.addYearAd(Int)
    func safeAddDayOfWeek(value: Int) -> DateAlone { return dateFrom(dateAlone: self, timeAlone: TimeAlone.noon).addDayOfWeek(value: value).dateAlone }
    func safeAddDayOfMonth(value: Int) -> DateAlone { return dateFrom(dateAlone: self, timeAlone: TimeAlone.noon).addDayOfMonth(value: value).dateAlone }
    func safeAddMonthOfYear(value: Int) -> DateAlone { return dateFrom(dateAlone: self, timeAlone: TimeAlone.noon).addMonthOfYear(value: value).dateAlone }
    func safeAddYearAd(value: Int) -> DateAlone { return dateFrom(dateAlone: self, timeAlone: TimeAlone.noon).addYearAd(value: value).dateAlone }
}

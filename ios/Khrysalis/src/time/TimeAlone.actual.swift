//Stub file made with Khrysalis 2 (by Lightning Kite)
import Foundation

//--- TimeAlone.{
//--- TimeAlone.Primary Constructor
//--- TimeAlone.Companion.{
//--- TimeAlone.Companion.now()
//--- TimeAlone.Companion.iso(String)
//--- TimeAlone.Companion.min
//--- TimeAlone.Companion.midnight
//--- TimeAlone.Companion.noon
//--- TimeAlone.Companion.max
//--- TimeAlone.Companion.}
//--- TimeAlone.comparable
//--- TimeAlone.secondsInDay
//--- TimeAlone.hoursInDay
//--- TimeAlone.}
public class TimeAlone: Equatable, Hashable, Codable {
    
    required public init(from decoder: Decoder) throws {
        let string: String = try decoder.singleValueContainer().decode(String.self)
        hour = Int(string.substringBefore(":"))!
        minute = Int(string.substringAfter(":").substringBefore(":"))!
        second = Int(string.substringAfterLast(":"))!
    }
    
    public func encode(to encoder: Encoder) throws {
        var container = encoder.singleValueContainer()
        try container.encode(iso8601())
    }
    
    public var hour: Int
    public var minute: Int
    public var second: Int
    
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
        hour: (Int)? = nil,
        minute: (Int)? = nil,
        second: (Int)? = nil
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
    
    static public func iso(string: String) -> TimeAlone? {
        if let hour = Int(string.substringBefore(":", "")),
            let minute = Int(string.substringAfter(":", "").substringBefore(":"))
        {
            let second = Int(string.substringAfter(":", "").substringAfter(":", ""))
            return TimeAlone(hour: hour, minute: minute, second: second ?? 0)
        }
        return nil
    }
    static public func iso(_ string: String) -> TimeAlone? {
        return iso(string: string)
    }
    static public var min = TimeAlone(0, 0, 0)
    static public var midnight = min
    static public var noon = TimeAlone(12, 0, 0)
    static public var max = TimeAlone(23, 59, 59)
    //End Companion
    
    public var comparable: Int {
        get {
            return self.hour * 60 * 60 + self.minute * 60 + self.second
        }
    }
    public var secondsInDay: Int {
        get {
            return self.hour * 60 * 60 + self.minute * 60 + self.second
        }
        set(value) {
            self.hour = value / 60 / 60
            self.minute = value / 60 % 60
            self.second = value % 60
        }
    }
    public var hoursInDay: Float {
        get {
            return Float(self.hour) + Float(self.minute) / Float(60) + Float(self.second) / Float(3600) + Float(0.5) / Float(3600)
        }
        set(value) {
            self.hour = Int(value)
            self.minute = Int( value * Float(60) ) % 60
            self.second = Int( value * Float(3600) ) % 60
        }
    }
    
    public init(hour: Int, minute: Int, second: Int) {
        self.hour = hour
        self.minute = minute
        self.second = second
    }
    convenience public init(_ hour: Int, _ minute: Int, _ second: Int) {
        self.init(hour: hour, minute: minute, second: second)
    }
}

//--- Extensions for TimeAlone
 
public extension TimeAlone {

    //--- TimeAlone.iso8601()
    func iso8601() -> String {
        let formatter = DateFormatter()
        formatter.locale = Locale(identifier: "en_US_POSIX")
        formatter.dateFormat = "HH:mm:ss"
        return formatter.string(from: dateFrom(dateAlone: DateAlone.now(), timeAlone: self))
    }
    
    //--- TimeAlone.minus(TimeAlone)
    static func - (left: TimeAlone, right: TimeAlone) -> TimeAlone{
        let result = left.hour * 60 * 60 + left.minute * 60 + left.second + right.hour * 60 * 60 + right.minute * 60 + right.second
        if result < 0{
            return TimeAlone(0,0,0)
        }else{
            return TimeAlone(result / 60 / 60, result / 60 % 60, result % 60)
        }
    }

    //--- TimeAlone.plus(TimeAlone)
    static func + (left: TimeAlone, right: TimeAlone) -> TimeAlone{
        let result = (left.hour * 60 * 60 + left.minute * 60 + left.second) + (right.hour * 60 * 60 + right.minute * 60 + right.second)
        return TimeAlone(result / 60 / 60,  result / 60 % 60, result % 60)
    }

    //--- TimeAlone.minus(TimeInterval)
    static func - (left: TimeAlone, right: TimeInterval) -> TimeAlone{
        let result = left.hour * 60 * 60 + left.minute * 60 + left.second - Int(right)
        if result < 0{
            return TimeAlone(0,0,0)
        }else{
            return TimeAlone(result / 60 / 60, result / 60 % 60, result % 60)
        }
    }

    //--- TimeAlone.plus(TimeInterval)
    static func + (left: TimeAlone, right: TimeInterval) -> TimeAlone{
        let result = (left.hour * 60 * 60 + left.minute * 60 + left.second) + Int(right)
        return TimeAlone(result / 60 / 60,  result / 60 % 60, result % 60)
    }

}

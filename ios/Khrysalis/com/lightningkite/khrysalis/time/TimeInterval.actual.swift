//Stub file made with Khrysalis 2 (by Lightning Kite)
import Foundation


//--- TimeInterval.{
//--- TimeInterval.Primary Constructor
//--- TimeInterval.milliseconds
public extension TimeInterval {
    var milliseconds: Int64 {
        return Int64(self * 1000)
    }
}
//--- TimeInterval.seconds
public extension TimeInterval {
    var seconds: Double {
        return self
    }
}
//--- TimeInterval.}

//--- Int.milliseconds()
//--- Int.seconds()
//--- Int.minutes()
//--- Int.hours()
//--- Int.days()
public extension Int {
    func milliseconds() -> TimeInterval { return TimeInterval(self) / 1000 }
    func seconds() -> TimeInterval { return TimeInterval(self) }
    func minutes() -> TimeInterval { return TimeInterval(self * 60) }
    func hours() -> TimeInterval { return TimeInterval(self * 60 * 60) }
    func days() -> TimeInterval { return TimeInterval(self * 60 * 60 * 24) }
}

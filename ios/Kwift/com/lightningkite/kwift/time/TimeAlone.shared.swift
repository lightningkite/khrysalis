//Package: com.lightningkite.kwift.time
//Converted using Kwift2

import Foundation



extension TimeAlone {
    public func normalize() -> Void {
        hour = ( hour + minute.floorDiv(60) ).floorMod(24)
        minute = ( minute + second.floorDiv(60) ).floorMod(60)
        second = second.floorMod(60)
    }
}
 
 

extension TimeAlone {
    public func set(other: TimeAlone) -> TimeAlone {
        self.hour = other.hour
        self.minute = other.minute
        self.second = other.second
        return self
    }
    public func set(_ other: TimeAlone) -> TimeAlone {
        return set(other: other)
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
 

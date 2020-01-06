//Package: com.lightningkite.kwift.time
//Converted using Kwift2

import Foundation



extension DateAlone {
    public func set(other: DateAlone) -> DateAlone {
        self.year = other.year
        self.month = other.month
        self.day = other.day
        return self
    }
    public func set(_ other: DateAlone) -> DateAlone {
        return set(other: other)
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
 

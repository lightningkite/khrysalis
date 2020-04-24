//Package: com.lightningkite.khrysalis.time
//Converted using Khrysalis2

import Foundation
import Khrysalis
import RxSwift
import RxRelay



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
 

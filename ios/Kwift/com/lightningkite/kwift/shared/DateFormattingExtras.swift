//Package: com.lightningkite.kwift.shared
//Converted using Kwift2

import Foundation



extension DateAlone {
    public func format(clockPartSize: ClockPartSize) -> String {
        return dateFrom(self, TimeAlone(0, 0, 0)).format(clockPartSize, ClockPartSize.None)
    }
    public func format(_ clockPartSize: ClockPartSize) -> String {
        return format(clockPartSize: clockPartSize)
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
 

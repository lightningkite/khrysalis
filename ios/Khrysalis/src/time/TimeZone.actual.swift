import Foundation

//--- TimeZone.{
public extension TimeZone {
    //--- TimeZone.id
    var id: String {
        return identifier
    }
    
    //--- TimeZone.displayName
    var displayName: String {
        return description
    }
    
    //--- TimeZone.getOffset(Long)
    func getOffset(_ date: Int64) -> Int {
        return Int(secondsFromGMT(for: Date(timeIntervalSince1970: Double(date) / 1000.0))) * 1000
    }
    func getOffset(date: Int64) -> Int {
        return getOffset(date)
    }
    
    //--- TimeZone.Companion.{
    
    //--- TimeZone.Companion.getDefault()
    static func getDefault() -> TimeZone {
        return TimeZone.autoupdatingCurrent
    }
    
    //--- TimeZone.Companion.}
    
    //--- TimeZone.}
}

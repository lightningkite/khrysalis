//Stub file made with Khrysalis 2 (by Lightning Kite)
import Foundation


//--- TimeNames.{
public class TimeNames {
    
    public static let INSTANCE = TimeNames()
    
    private var formatter = DateFormatter()
    
    //--- TimeNames.shortMonthNames
    public lazy var shortMonthNames = formatter.shortMonthSymbols!
    
    //--- TimeNames.monthNames
    public lazy var monthNames = formatter.monthSymbols!
    
    //--- TimeNames.shortWeekdayNames
    public  lazy var shortWeekdayNames = formatter.shortWeekdaySymbols!
    
    //--- TimeNames.weekdayNames
    public  lazy var weekdayNames = formatter.weekdaySymbols!
    
    //--- TimeNames.shortMonthName(Int)
    public func shortMonthName(oneIndexedPosition: Int) -> String {
        return shortMonthNames[oneIndexedPosition - 1]
    }
    
    //--- TimeNames.monthName(Int)
    public func monthName(oneIndexedPosition: Int) -> String {
        return monthNames[oneIndexedPosition - 1]
    }
    
    //--- TimeNames.shortWeekdayName(Int)
    public func shortWeekdayName(oneIndexedPosition: Int) -> String {
        return shortWeekdayNames[oneIndexedPosition - 1]
    }
    
    //--- TimeNames.weekdayName(Int)
    public func weekdayName(oneIndexedPosition: Int) -> String {
        return weekdayNames[oneIndexedPosition - 1]
    }
    
    //--- TimeNames.}
}

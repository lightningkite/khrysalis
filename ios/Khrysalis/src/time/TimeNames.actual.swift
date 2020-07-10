//Stub file made with Khrysalis 2 (by Lightning Kite)
import Foundation


//--- TimeNames.{
public enum TimeNames {
    private static var formatter = DateFormatter()
    
    //--- TimeNames.shortMonthNames
    public static var shortMonthNames = formatter.shortMonthSymbols!
    
    //--- TimeNames.monthNames
    public static var monthNames = formatter.monthSymbols!
    
    //--- TimeNames.shortWeekdayNames
    public static var shortWeekdayNames = formatter.shortWeekdaySymbols!
    
    //--- TimeNames.weekdayNames
    public static var weekdayNames = formatter.weekdaySymbols!
    
    //--- TimeNames.shortMonthName(Int)
    public static func shortMonthName(_ oneIndexedPosition: Int) -> String {
        return shortMonthNames[oneIndexedPosition - 1]
    }
    public static func shortMonthName(oneIndexedPosition: Int) -> String {
        return shortMonthName(oneIndexedPosition)
    }
    
    //--- TimeNames.monthName(Int)
    public static func monthName(_ oneIndexedPosition: Int) -> String {
        return monthNames[oneIndexedPosition - 1]
    }
    public static func monthName(oneIndexedPosition: Int) -> String {
        return monthName(oneIndexedPosition)
    }
    
    //--- TimeNames.shortWeekdayName(Int)
    public static func shortWeekdayName(_ oneIndexedPosition: Int) -> String {
        return shortWeekdayNames[oneIndexedPosition - 1]
    }
    public static func shortWeekdayName(oneIndexedPosition: Int) -> String {
        return shortWeekdayName(oneIndexedPosition)
    }
    
    //--- TimeNames.weekdayName(Int)
    public static func weekdayName(_ oneIndexedPosition: Int) -> String {
        return weekdayNames[oneIndexedPosition - 1]
    }
    public static func weekdayName(oneIndexedPosition: Int) -> String {
        return weekdayName(oneIndexedPosition)
    }
    
    //--- TimeNames.}
}

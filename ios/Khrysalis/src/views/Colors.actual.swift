//Stub file made with Khrysalis 2 (by Lightning Kite)
import Foundation
import UIKit


//--- ColorValue
public typealias ColorValue = UIColor

//--- Int.asColor()
public extension Int32 {
    func asColor() -> UIColor {
        return UIColor(argb: Int(self))
    }
}

//--- Long.asColor()
public extension Int64 {
    func asColor() -> UIColor {
        return UIColor(argb: Int(self))
    }
}
public extension Int {
    func asColor() -> UIColor {
        return UIColor(argb: Int(self))
    }
}

//--- ColorValue.colorAlpha(Int)
public extension ColorValue {
    convenience init(red: Int, green: Int, blue: Int, alpha: Int = 0xFF) {
        self.init(
            red: CGFloat(red) / 255.0,
            green: CGFloat(green) / 255.0,
            blue: CGFloat(blue) / 255.0,
            alpha: CGFloat(alpha) / 255.0
        )
    }
    
    // let's suppose alpha is the first component (ARGB)
    convenience init(argb: Int) {
        self.init(
            red: (argb >> 16) & 0xFF,
            green: (argb >> 8) & 0xFF,
            blue: argb & 0xFF,
            alpha: (argb >> 24) & 0xFF
        )
    }
    
    func colorAlpha(desiredAlpha: Int) -> UIColor {
        return withAlphaComponent(CGFloat(desiredAlpha)/255)
    }

    var rgba: (red: CGFloat, green: CGFloat, blue: CGFloat, alpha: CGFloat) {
        var red: CGFloat = 0
        var green: CGFloat = 0
        var blue: CGFloat = 0
        var alpha: CGFloat = 0
        getRed(&red, green: &green, blue: &blue, alpha: &alpha)

        return (red, green, blue, alpha)
    }
}

//Stub file made with Khrysalis 2 (by Lightning Kite)
import Foundation
import UIKit
import CoreGraphics


//--- Paint.{
public class Paint {
    
    //--- Paint.Primary Constructor
    public init() {
    }
    
    //--- Paint.flags
    public var flags: Int = 0

    //--- Paint.color
    public var color: ColorValue = UIColor.black

    //--- Paint.strokeWidth
    public var strokeWidth: CGFloat = 0

    //--- Paint.alpha
    public var alpha: Int = 255

    //--- Paint.style
    public var style: Style = .FILL_AND_STROKE

    //--- Paint.textSize
    public var textSize: CGFloat = 12

    //--- Paint.shader
    public var shader: ShaderValue? = nil

    //--- Paint.isAntiAlias
    public var isAntiAlias: Bool = false

    //--- Paint.isFakeBoldText
    public var isFakeBoldText: Bool = false

    //--- Paint.Style.{
    //--- Paint.Style.}
    public enum Style { case FILL, STROKE, FILL_AND_STROKE }

    //--- Paint.measureText(String)
    private struct MeasureTextCacheKey: Hashable {
        var text: String
        var textSize: CGFloat
    }
    static private var measureText_cache: Dictionary<MeasureTextCacheKey, CGFloat> = Dictionary()
    public func measureText(_ text: String) -> CGFloat {
        let key = MeasureTextCacheKey(text: text, textSize: textSize)
        if let result = Paint.measureText_cache[key] {
            return result
        }
        let result = CGFloat(NSString(string: text).size(withAttributes: attributes).width)
        Paint.measureText_cache[key] = result
        return result
    }
    public func measureText(text: String) -> CGFloat {
        return measureText(text)
    }

    //--- Paint.textHeight
    public var textHeight: CGFloat {
        let font = UIFont.get(size: CGFloat(textSize), style: [])
        return CGFloat(font.lineHeight)
    }
    
    //--- Paint.attributes
    var attributes: Dictionary<NSAttributedString.Key, Any> {
        return [
            .font: UIFont.get(size: CGFloat(textSize), style: isFakeBoldText ? ["bold"] : []),
            .foregroundColor: color
        ]
    }
    
    //--- Paint.}
}

//Stub file made with Khrysalis 2 (by Lightning Kite)
import Foundation
import UIKit


//--- RectF.{
public typealias RectF = CGRect
public extension RectF {
    
    init(){
        self = .zero
    }

    init(_ left: CGFloat, _ top: CGFloat, _ right: CGFloat, _ bottom: CGFloat) {
        self.init(x: left, y: top, width: right - left, height: bottom - top)
    }

    //--- RectF.right
    var right: CGFloat {
        get {
            return CGFloat(origin.x + size.width)
        }
        set(value){
            size.width = CGFloat(value) - origin.x
        }
    }
    //--- RectF.bottom
    var bottom: CGFloat {
        get {
            return CGFloat(origin.y + size.height)
        }
        set(value){
            size.height = CGFloat(value) - origin.y
        }
    }
    //--- RectF.left
    var left: CGFloat {
        get {
            return CGFloat(origin.x)
        }
        set(value){
            let cg = CGFloat(value)
            size.width -= cg - origin.x
            origin.x = cg
        }
    }
    //--- RectF.top
    var top: CGFloat {
        get {
            return CGFloat(origin.y)
        }
        set(value){
            let cg = CGFloat(value)
            size.height -= cg - origin.y
            origin.y = cg
        }
    }
    //--- RectF.set(CGFloat, CGFloat, CGFloat, CGFloat)
    mutating func set(_ left: CGFloat, _ top: CGFloat, _ right: CGFloat, _ bottom: CGFloat) {
        origin.x = CGFloat(left)
        origin.y = CGFloat(top)
        size.width = CGFloat(right - left)
        size.height = CGFloat(bottom - top)
    }
    //--- RectF.set(RectF)
    mutating func set(_ rect: RectF) {
        origin.x = rect.origin.x
        origin.y = rect.origin.y
        size.width = rect.size.width
        size.height = rect.size.height
    }
    //--- RectF.centerX()
    func centerX() -> CGFloat {
        return CGFloat(midX)
    }
    //--- RectF.centerY()
    func centerY() -> CGFloat {
        return CGFloat(midY)
    }
    //--- RectF.width()
    func width() -> CGFloat {
        return CGFloat(size.width)
    }
    //--- RectF.height()
    func height() -> CGFloat {
        return CGFloat(size.height)
    }
    //--- RectF.inset(CGFloat, CGFloat)
    mutating func inset(_ dx: CGFloat, _ dy: CGFloat) {
        self = self.insetBy(dx: CGFloat(dx), dy: CGFloat(dy))
    }
    
    //--- RectF.}
}

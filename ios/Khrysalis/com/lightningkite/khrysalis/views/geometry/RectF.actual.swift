//Stub file made with Khrysalis 2 (by Lightning Kite)
import Foundation
import UIKit


//--- RectF.{
public typealias RectF = CGRect
public extension RectF {
    
    init(){
        self = .zero
    }
    //--- RectF.right
    var right: GFloat {
        get {
            return GFloat(origin.x + size.width)
        }
        set(value){
            size.width = CGFloat(value) - origin.x
        }
    }
    //--- RectF.bottom
    var bottom: GFloat {
        get {
            return GFloat(origin.y + size.height)
        }
        set(value){
            size.height = CGFloat(value) - origin.y
        }
    }
    //--- RectF.left
    var left: GFloat {
        get {
            return GFloat(origin.x)
        }
        set(value){
            let cg = CGFloat(value)
            size.width -= cg - origin.x
            origin.x = cg
        }
    }
    //--- RectF.top
    var top: GFloat {
        get {
            return GFloat(origin.y)
        }
        set(value){
            let cg = CGFloat(value)
            size.height -= cg - origin.y
            origin.y = cg
        }
    }
    //--- RectF.set(GFloat, GFloat, GFloat, GFloat)
    mutating func set(_ left: GFloat, _ top: GFloat, _ right: GFloat, _ bottom: GFloat) {
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
    func centerX() -> GFloat {
        return GFloat(midX)
    }
    //--- RectF.centerY()
    func centerY() -> GFloat {
        return GFloat(midY)
    }
    //--- RectF.width()
    func width() -> GFloat {
        return GFloat(size.width)
    }
    //--- RectF.height()
    func height() -> GFloat {
        return GFloat(size.height)
    }
    //--- RectF.inset(GFloat, GFloat)
    mutating func inset(_ dx: GFloat, _ dy: GFloat) {
        self = self.insetBy(dx: CGFloat(dx), dy: CGFloat(dy))
    }
    
    //--- RectF.}
}

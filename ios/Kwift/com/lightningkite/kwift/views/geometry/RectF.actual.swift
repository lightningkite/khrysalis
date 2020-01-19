//Stub file made with Kwift 2 (by Lightning Kite)
import Foundation
import UIKit


//--- RectF.{
public typealias RectF = CGRect
public extension RectF {
    
    init(){
        self = .zero
    }
    //--- RectF.right
    var right: Float {
        get {
            return Float(origin.x + size.width)
        }
        set(value){
            size.width = CGFloat(value) - origin.x
        }
    }
    //--- RectF.bottom
    var bottom: Float {
        get {
            return Float(origin.y + size.height)
        }
        set(value){
            size.height = CGFloat(value) - origin.y
        }
    }
    //--- RectF.left
    var left: Float {
        get {
            return Float(origin.x)
        }
        set(value){
            let cg = CGFloat(value)
            size.width -= cg - origin.x
            origin.x = cg
        }
    }
    //--- RectF.top
    var top: Float {
        get {
            return Float(origin.y)
        }
        set(value){
            let cg = CGFloat(value)
            size.height -= cg - origin.y
            origin.y = cg
        }
    }
    //--- RectF.set(Float, Float, Float, Float)
    mutating func set(_ left: Float, _ top: Float, _ right: Float, _ bottom: Float) {
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
    func centerX() -> Float {
        return Float(midX)
    }
    //--- RectF.centerY()
    func centerY() -> Float {
        return Float(midY)
    }
    //--- RectF.width()
    func width() -> Float {
        return Float(size.width)
    }
    //--- RectF.height()
    func height() -> Float {
        return Float(size.height)
    }
    //--- RectF.inset(Float, Float)
    mutating func inset(_ dx: Float, _ dy: Float) {
        self = self.insetBy(dx: CGFloat(dx), dy: CGFloat(dy))
    }
    
    //--- RectF.}
}














//Package: com.lightningkite.kwift.views.shared
//Converted using Kwift2

import Foundation



public protocol CustomViewDelegate {
    
    func generateAccessibilityView() -> View?
    
    func draw(canvas: Canvas, width: Float, height: Float) -> Void
    func draw(_ canvas: Canvas, _ width: Float, _ height: Float) -> Void
    
    func onTouchDown(id: Int32, x: Float, y: Float, width: Float, height: Float) -> Bool
    func onTouchDown(_ id: Int32, _ x: Float, _ y: Float, _ width: Float, _ height: Float) -> Bool
    
    func onTouchMove(id: Int32, x: Float, y: Float, width: Float, height: Float) -> Bool
    func onTouchMove(_ id: Int32, _ x: Float, _ y: Float, _ width: Float, _ height: Float) -> Bool
    
    func onTouchUp(id: Int32, x: Float, y: Float, width: Float, height: Float) -> Bool
    func onTouchUp(_ id: Int32, _ x: Float, _ y: Float, _ width: Float, _ height: Float) -> Bool
}

public extension CustomViewDelegate {
    
    func onTouchDown(id: Int32, x: Float, y: Float, width: Float, height: Float) -> Bool {
        return false
    }
    func onTouchDown(_ id: Int32, _ x: Float, _ y: Float, _ width: Float, _ height: Float) -> Bool {
        return onTouchDown(id: id, x: x, y: y, width: width, height: height)
    }
    
    func onTouchMove(id: Int32, x: Float, y: Float, width: Float, height: Float) -> Bool {
        return false
    }
    func onTouchMove(_ id: Int32, _ x: Float, _ y: Float, _ width: Float, _ height: Float) -> Bool {
        return onTouchMove(id: id, x: x, y: y, width: width, height: height)
    }
    
    func onTouchUp(id: Int32, x: Float, y: Float, width: Float, height: Float) -> Bool {
        return false
    }
    func onTouchUp(_ id: Int32, _ x: Float, _ y: Float, _ width: Float, _ height: Float) -> Bool {
        return onTouchUp(id: id, x: x, y: y, width: width, height: height)
    }
}
 

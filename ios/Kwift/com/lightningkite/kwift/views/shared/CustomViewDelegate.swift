//Package: com.lightningkite.kwift.views.shared
//Converted using Kwift2

import Foundation



open class CustomViewDelegate {
    
    
    public weak var customView: CustomView? 
    
    open func generateAccessibilityView() -> View? { fatalError() }
    
    open func draw(canvas: Canvas, width: Float, height: Float, displayMetrics: DisplayMetrics) -> Void { fatalError() }
    open func draw(_ canvas: Canvas, _ width: Float, _ height: Float, _ displayMetrics: DisplayMetrics) -> Void { fatalError() }
    
    open func onTouchDown(id: Int32, x: Float, y: Float, width: Float, height: Float) -> Bool {
        return false
    }
    open func onTouchDown(_ id: Int32, _ x: Float, _ y: Float, _ width: Float, _ height: Float) -> Bool {
        return onTouchDown(id: id, x: x, y: y, width: width, height: height)
    }
    
    open func onTouchMove(id: Int32, x: Float, y: Float, width: Float, height: Float) -> Bool {
        return false
    }
    open func onTouchMove(_ id: Int32, _ x: Float, _ y: Float, _ width: Float, _ height: Float) -> Bool {
        return onTouchMove(id: id, x: x, y: y, width: width, height: height)
    }
    
    open func onTouchUp(id: Int32, x: Float, y: Float, width: Float, height: Float) -> Bool {
        return false
    }
    open func onTouchUp(_ id: Int32, _ x: Float, _ y: Float, _ width: Float, _ height: Float) -> Bool {
        return onTouchUp(id: id, x: x, y: y, width: width, height: height)
    }
    
    open func sizeThatFitsWidth(width: Float, height: Float) -> Float {
        return width
    }
    open func sizeThatFitsWidth(_ width: Float, _ height: Float) -> Float {
        return sizeThatFitsWidth(width: width, height: height)
    }
    
    open func sizeThatFitsHeight(width: Float, height: Float) -> Float {
        return height
    }
    open func sizeThatFitsHeight(_ width: Float, _ height: Float) -> Float {
        return sizeThatFitsHeight(width: width, height: height)
    }
    
    public func invalidate() -> Void {
        customView?.invalidate()
    }
    
    public func postInvalidate() -> Void {
        customView?.postInvalidate()
    }
    
    public init() {
        let customView: CustomView?  = nil
        self.customView = customView
    }
}
 

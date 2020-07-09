//Package: com.lightningkite.khrysalis.views
//Converted using Khrysalis2

import Foundation
import RxSwift
import RxRelay



open class CustomViewDelegate {
    
    
    public weak var customView: CustomView? 
    
    open func generateAccessibilityView() -> View? { fatalError() }
    
    open func draw(canvas: Canvas, width: CGFloat, height: CGFloat, displayMetrics: DisplayMetrics) -> Void { fatalError() }
    open func draw(_ canvas: Canvas, _ width: CGFloat, _ height: CGFloat, _ displayMetrics: DisplayMetrics) -> Void { fatalError() }
    
    open func onTouchDown(id: Int, x: CGFloat, y: CGFloat, width: CGFloat, height: CGFloat) -> Bool {
        return false
    }
    open func onTouchDown(_ id: Int, _ x: CGFloat, _ y: CGFloat, _ width: CGFloat, _ height: CGFloat) -> Bool {
        return onTouchDown(id: id, x: x, y: y, width: width, height: height)
    }
    
    open func onTouchMove(id: Int, x: CGFloat, y: CGFloat, width: CGFloat, height: CGFloat) -> Bool {
        return false
    }
    open func onTouchMove(_ id: Int, _ x: CGFloat, _ y: CGFloat, _ width: CGFloat, _ height: CGFloat) -> Bool {
        return onTouchMove(id: id, x: x, y: y, width: width, height: height)
    }
    
    open func onTouchCancelled(id: Int, x: CGFloat, y: CGFloat, width: CGFloat, height: CGFloat) -> Bool {
        return false
    }
    open func onTouchCancelled(_ id: Int, _ x: CGFloat, _ y: CGFloat, _ width: CGFloat, _ height: CGFloat) -> Bool {
        return onTouchCancelled(id: id, x: x, y: y, width: width, height: height)
    }
    
    open func onTouchUp(id: Int, x: CGFloat, y: CGFloat, width: CGFloat, height: CGFloat) -> Bool {
        return false
    }
    open func onTouchUp(_ id: Int, _ x: CGFloat, _ y: CGFloat, _ width: CGFloat, _ height: CGFloat) -> Bool {
        return onTouchUp(id: id, x: x, y: y, width: width, height: height)
    }
    
    open func sizeThatFitsWidth(width: CGFloat, height: CGFloat) -> CGFloat {
        return width
    }
    open func sizeThatFitsWidth(_ width: CGFloat, _ height: CGFloat) -> CGFloat {
        return sizeThatFitsWidth(width: width, height: height)
    }
    
    open func sizeThatFitsHeight(width: CGFloat, height: CGFloat) -> CGFloat {
        return height
    }
    open func sizeThatFitsHeight(_ width: CGFloat, _ height: CGFloat) -> CGFloat {
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
 

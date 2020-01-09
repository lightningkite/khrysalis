//Stub file made with Kwift 2 (by Lightning Kite)
import Foundation
import UIKit
import CoreGraphics


//--- CustomView.{
public class CustomView: FrameLayout {
    
    //--- CustomView.Primary Constructor
    
    //--- CustomView.delegate
    public var delegate: CustomViewDelegate? {
        willSet {
            isOpaque = false
            delegate?.customView = nil
        }
        didSet {
            delegate?.customView = self
            self.isUserInteractionEnabled = true
            self.isMultipleTouchEnabled = true
            if UIAccessibility.isVoiceOverRunning {
                if let accessibilityView = delegate?.generateAccessibilityView() {
                    addSubview(accessibilityView, gravity: .fillFill)
                    self.accessibilityView = accessibilityView
                }
            }
        }
    }
    
    //--- CustomView.accessibilityView
    public weak var accessibilityView: UIView?
    
    //--- CustomView implementation
    let scaleInformation = DisplayMetrics(
        density: Float(UIScreen.main.scale),
        scaledDensity: Float(UIScreen.main.scale),
        widthPixels: Int32(UIScreen.main.bounds.width * UIScreen.main.scale),
        heightPixels: Int32(UIScreen.main.bounds.height * UIScreen.main.scale)
    )
    
    override public func draw(_ rect: CGRect) {
        guard let ctx = UIGraphicsGetCurrentContext() else { return }
//        ctx.clear(rect)
        ctx.scale(1/scaleInformation.density, 1/scaleInformation.density)
        delegate?.draw(ctx, Float(rect.size.width) * scaleInformation.density, Float(rect.size.height) * scaleInformation.density, scaleInformation)
    }
    
    private var touchIds = Dictionary<UITouch, Int32>()
    private var currentTouchId: Int32 = 0
    override public func touchesBegan(_ touches: Set<UITouch>, with event: UIEvent?) {
        handleTouches(touches)
        super.touchesBegan(touches, with: event)
    }

    override public func touchesMoved(_ touches: Set<UITouch>, with event: UIEvent?) {
        handleTouches(touches)
        super.touchesMoved(touches, with: event)
    }

    override public func touchesEnded(_ touches: Set<UITouch>, with event: UIEvent?) {
        handleTouches(touches)
        super.touchesEnded(touches, with: event)
    }
    
    override public func sizeThatFits(_ size: CGSize) -> CGSize {
        if let delegate = delegate {
            return CGSize(
                width: CGFloat(delegate.sizeThatFitsWidth(Float(size.width), Float(size.height))),
                height: CGFloat(delegate.sizeThatFitsHeight(Float(size.width), Float(size.height)))
            )
        } else {
            return super.sizeThatFits(size)
        }
    }
    
    private func handleTouches(_ touches: Set<UITouch>){
        for touch in touches {
            let loc = touch.location(in: self)
            switch touch.phase {
            case .began:
                let id = currentTouchId
                currentTouchId += 1
                touchIds[touch] = id
                let _ = delegate?.onTouchDown(
                    id,
                    Float(loc.x) * scaleInformation.density,
                    Float(loc.y) * scaleInformation.density,
                    Float(frame.size.width) * scaleInformation.density,
                    Float(frame.size.height) * scaleInformation.density
                )
            case .moved:
                if let id = touchIds[touch] {
                    let _ = delegate?.onTouchMove(
                        id,
                        Float(loc.x) * scaleInformation.density,
                        Float(loc.y) * scaleInformation.density,
                        Float(frame.size.width) * scaleInformation.density,
                        Float(frame.size.height) * scaleInformation.density
                    )
                }
            case .ended:
                if let id = touchIds[touch] {
                    let _ = delegate?.onTouchUp(
                        id,
                        Float(loc.x) * scaleInformation.density,
                        Float(loc.y) * scaleInformation.density,
                        Float(frame.size.width) * scaleInformation.density,
                        Float(frame.size.height) * scaleInformation.density
                    )
                }
                touchIds.removeValue(forKey: touch)
            default:
                break
            }
        }
    }
    
    //--- CustomView.}
}






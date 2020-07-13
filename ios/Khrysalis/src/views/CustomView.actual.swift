//Stub file made with Khrysalis 2 (by Lightning Kite)
import Foundation
import UIKit
import CoreGraphics


//--- CustomView.{
public class CustomView: FrameLayout {
    
    //--- CustomView.Primary Constructor
    
    //--- CustomView.delegate
    private var disposeSet: Bool = false
    public var delegate: CustomViewDelegate? {
        willSet {
            isOpaque = false
            delegate?.customView = nil
//            delegate?.dispose()
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
            if !disposeSet {
                disposeSet = true
                self.removed.call(DisposableLambda {
                    self.delegate = nil
                })
            }
        }
    }
    
    //--- CustomView.accessibilityView
    public weak var accessibilityView: UIView?
    
    //--- CustomView implementation
    let scaleInformation = DisplayMetrics(
        density: (UIScreen.main.scale),
        scaledDensity: (UIScreen.main.scale),
        widthPixels: Int(UIScreen.main.bounds.width * UIScreen.main.scale),
        heightPixels: Int(UIScreen.main.bounds.height * UIScreen.main.scale)
    )
    
    override public func draw(_ rect: CGRect) {
        guard let ctx = UIGraphicsGetCurrentContext() else { return }
//        ctx.clear(rect)
        ctx.scale(1/scaleInformation.density, 1/scaleInformation.density)
        delegate?.draw(canvas: ctx, width: (rect.size.width) * scaleInformation.density, height: (rect.size.height) * scaleInformation.density, displayMetrics: scaleInformation)
    }
    
    private var touchIds = Dictionary<UITouch, Int>()
    private var currentTouchId: Int = 0
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
                width: CGFloat(delegate.sizeThatFitsWidth(width: (size.width), height: (size.height))),
                height: CGFloat(delegate.sizeThatFitsHeight(width: (size.width), height: (size.height)))
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
                    id: id,
                    x: (loc.x) * scaleInformation.density,
                    y: (loc.y) * scaleInformation.density,
                    width: (frame.size.width) * scaleInformation.density,
                    height: (frame.size.height) * scaleInformation.density
                )
            case .moved:
                if let id = touchIds[touch] {
                    let _ = delegate?.onTouchMove(
                        id: id,
                        x: (loc.x) * scaleInformation.density,
                        y: (loc.y) * scaleInformation.density,
                        width: (frame.size.width) * scaleInformation.density,
                        height: (frame.size.height) * scaleInformation.density
                    )
                }
            case .ended:
                if let id = touchIds[touch] {
                    let _ = delegate?.onTouchUp(
                        id: id,
                        x: (loc.x) * scaleInformation.density,
                        y: (loc.y) * scaleInformation.density,
                        width: (frame.size.width) * scaleInformation.density,
                        height: (frame.size.height) * scaleInformation.density
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

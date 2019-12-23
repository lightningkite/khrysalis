//
//  CustomView.swift
//  Alamofire
//
//  Created by Joseph Ivie on 12/23/19.
//

import UIKit


public class CustomView: FrameLayout {
    
    public var delegate: CustomViewDelegate?
    public weak var accessibilityView: UIView?
    
    public func setup(){
        self.isUserInteractionEnabled = true
        self.isMultipleTouchEnabled = true
        if UIAccessibility.isVoiceOverRunning {
            if let accessibilityView = delegate?.generateAccessibilityView() {
                addSubview(accessibilityView, gravity: .fillFill)
                self.accessibilityView = accessibilityView
            }
        }
    }
    
    override public func draw(_ rect: CGRect) {
        guard let ctx = UIGraphicsGetCurrentContext() else { return }
        ctx.clear(rect)
        delegate?.draw(ctx, Float(rect.size.width), Float(rect.size.height))
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
    
    private func handleTouches(_ touches: Set<UITouch>){
        for touch in touches {
            let loc = touch.location(in: self)
            switch touch.phase {
            case .began:
                let id = currentTouchId
                currentTouchId += 1
                touchIds[touch] = id
                let _ = delegate?.onTouchDown(id, Float(loc.x), Float(loc.y), Float(frame.size.width), Float(frame.size.height))
            case .moved:
                if let id = touchIds[touch] {
                    let _ = delegate?.onTouchMove(id, Float(loc.x), Float(loc.y), Float(frame.size.width), Float(frame.size.height))
                }
            case .ended:
                if let id = touchIds[touch] {
                    let _ = delegate?.onTouchUp(id, Float(loc.x), Float(loc.y), Float(frame.size.width), Float(frame.size.height))
                }
                touchIds.removeValue(forKey: touch)
            default:
                break
            }
        }
    }
}

//
//  SwapView.swift
//  Khrysalis
//
//  Created by Joseph Ivie on 10/24/19.
//  Copyright © 2019 Lightning Kite. All rights reserved.
//

import Foundation
import UIKit


open class SwapView: UIView {
    public enum Animation {
        case push
        case pop
        case fade
    }
    struct AnimationGoal {
        let startedAt: Date
        let alpha: CGFloat
        let scaledFrame: CGRect
        var frame: CGRect?
        var completion: (UIView)->Void = { _ in }
    }
    
    let animateDestinationExtension = ExtensionProperty<UIView, AnimationGoal>()
    var current: UIView?
    
    override public func sizeThatFits(_ size: CGSize) -> CGSize {
        return current?.sizeThatFits(size) ?? .zero
    }
    
    override open func setNeedsLayout() {
        super.setNeedsLayout()
        self.notifyParentSizeChanged()
    }
    
    override public func layoutSubviews() {
        updateAnimations()
    }
    
    public override func willRemoveSubview(_ subview: UIView) {
        post {
            subview.refreshLifecycle()
        }
    }
    
    public override func didAddSubview(_ subview: UIView) {
        subview.refreshLifecycle()
    }
    
    private var hiding = false

    private func updateAnimations(){
        for view in subviews {
            guard var goal = animateDestinationExtension.get(view) else { continue }
            let toFrame = CGRect(
                x: goal.scaledFrame.origin.x * self.bounds.width,
                y: goal.scaledFrame.origin.y * self.bounds.height,
                width: goal.scaledFrame.size.width * self.bounds.width,
                height: goal.scaledFrame.size.height * self.bounds.height
            )
            guard goal.frame != toFrame else { continue }
            goal.frame = toFrame
            animateDestinationExtension.set(view, goal)
            UIView.animate(
                withDuration: 0.25,
                animations: {
                    view.alpha = goal.alpha
                    view.frame = toFrame
                    KhrysalisViewController.refreshBackgroundColorEvent.invokeAll(())
                },
                completion: { done in
                    view.setNeedsLayout()
                    goal.completion(view)
                }
            )
        }
    }
    open func swap(dependency: ViewDependency, to: UIView?, animation: Animation){
        let previousView = current
        if let old = current {
            let goal: AnimationGoal
            if to == nil {
                goal = AnimationGoal(
                    startedAt: Date(),
                    alpha: 0.0,
                    scaledFrame: CGRect(x: 0, y: 0, width: 1, height: 1),
                    frame: nil,
                    completion: { view in view.removeFromSuperview() }
                )
            } else {
                switch animation {
                case .fade:
                    goal = AnimationGoal(
                        startedAt: Date(),
                        alpha: 0.0,
                        scaledFrame: CGRect(x: 0, y: 0, width: 1, height: 1),
                        frame: nil,
                        completion: { view in view.removeFromSuperview() }
                    )
                case .pop:
                    goal = AnimationGoal(
                        startedAt: Date(),
                        alpha: 1.0,
                        scaledFrame: CGRect(x: 1, y: 0, width: 1, height: 1),
                        frame: nil,
                        completion: { view in view.removeFromSuperview() }
                    )
                case .push:
                    goal = AnimationGoal(
                        startedAt: Date(),
                        alpha: 1.0,
                        scaledFrame: CGRect(x: -1, y: 0, width: 1, height: 1),
                        frame: nil,
                        completion: { view in view.removeFromSuperview() }
                    )
                }
            }
            animateDestinationExtension.set(old, goal)
            updateAnimations()
        }
        if let new = to {
            if self.hiding {
                visibility = View.VISIBLE
                self.hiding = false
                alpha = 1
                print("Am I in layout? \(includeInLayout)")
                setNeedsLayout()
                UIView.animate(withDuration: 0.25, animations: {
                    self.alpha = 1
                }, completion: { _ in
                    print("My bounds are now \(self.frame)")
                })
                new.frame = CGRect(
                    x: 0,
                    y: 0,
                    width: self.bounds.width,
                    height: self.bounds.height
                )
            } else {
                switch animation {
                case .fade:
                    new.frame = CGRect(
                        x: 0,
                        y: 0,
                        width: self.bounds.width,
                        height: self.bounds.height
                    )
                    new.alpha = 0.0
                case .pop:
                    new.frame = CGRect(
                        x: -self.bounds.width,
                        y: 0,
                        width: self.bounds.width,
                        height: self.bounds.height
                    )
                case .push:
                    new.frame = CGRect(
                        x: self.bounds.width,
                        y: 0,
                        width: self.bounds.width,
                        height: self.bounds.height
                    )
                }
            }
            new.setNeedsLayout()
            new.layoutIfNeeded()
            self.addSubview(new)
            animateDestinationExtension.set(new, AnimationGoal(
                startedAt: Date(),
                alpha: 1.0,
                scaledFrame: CGRect(x: 0, y: 0, width: 1, height: 1),
                frame: nil,
                completion: { _ in }
            ))
            updateAnimations()
            current = new
        } else {
            current = nil
            print("Am I in layout? \(includeInLayout)")
            hiding = true
            //This has gotta stick
            print("My bounds are now \(self.frame)")
            if self.hiding {
                self.visibility = View.INVISIBLE
            } else {
                self.alpha = 1
                self.visibility = View.VISIBLE
            }
        }
        dependency.runKeyboardUpdate(root: to, discardingRoot: previousView)
    }
    
    weak var lastHit: View?
    var lastPoint: CGPoint?
    override open func hitTest(_ point: CGPoint, with event: UIEvent?) -> UIView? {
        lastPoint = point
        for key in subviews.reversed() {
            guard !key.isHidden, key.alpha > 0.1, key.includeInLayout else { continue }
            if key.frame.contains(point) {
                lastHit = key
                if let sub = key.hitTest(key.convert(point, from: self), with: event) {
                    return sub
                } else {
                    return key
                }
            }
        }
        return nil
    }}


//
//  UIView+swap.swift
//  KwiftTemplate
//
//  Created by Joseph Ivie on 8/21/19.
//  Copyright © 2019 Joseph Ivie. All rights reserved.
//

import Foundation
import UIKit

public extension UIView {
    enum Animation {
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
    func bindStack(_ dependency: ViewDependency, _ stack: ObservableStack<ViewGenerator>) {
        return bindStack(dependency: dependency, stack: stack)
    }
    func bindStack(dependency: ViewDependency, stack: ObservableStack<ViewGenerator>) {
        var current: UIView? = nil
        var lastCount = 0
        var animateDestinationExtension = ExtensionProperty<UIView, AnimationGoal>()
        func updateAnimations(){
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
                    },
                    completion: { done in
                        view.setNeedsLayout()
                        goal.completion(view)
                    }
                )
            }
        }
        self.addOnLayoutSubviews {
            updateAnimations()
        }
        stack.addAndRunWeak(self) { this, value in
            var animation = Animation.fade
            if lastCount == 0 {
                animation = .fade
            } else if value.count > lastCount {
                animation = .push
            } else if value.count < lastCount {
                animation = .pop
            }
            lastCount = value.count

            if let old = current {
                let goal: AnimationGoal
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
                animateDestinationExtension.set(old, goal)
                updateAnimations()
            }
            if let newData = value.last {
                if this.isHidden {
                    this.isHidden = false
                    this.setNeedsLayout()
                }
                let new = newData.generate(dependency: dependency)
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
                new.setNeedsLayout()
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
                this.bounds.size = CGSize(width: 1, height: 1)
                this.isHidden = true
            }
        }
    }
}

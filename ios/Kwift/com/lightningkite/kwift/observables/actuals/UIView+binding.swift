//
//  UIView+binding.swift
//  KwiftTemplate
//
//  Created by Joseph Ivie on 8/21/19.
//  Copyright © 2019 Joseph Ivie. All rights reserved.
//

import Foundation
import UIKit
import FlexLayout

extension UIView {
    func bindVisible(_ observable: ObservableProperty<Bool>) {
        return bindVisible(observable: observable)
    }
    func bindVisible(observable: ObservableProperty<Bool>) {
        observable.addAndRunWeak(referenceA: self) { (this, value) in
            this.isHidden = !value
        }
    }
    
    func bindExists(_ observable: ObservableProperty<Bool>) {
        return bindExists(observable: observable)
    }
    func bindExists(observable: ObservableProperty<Bool>) {
        observable.addAndRunWeak(referenceA: self) { (this, value) in
            if value {
                this.flex.display(.flex)
            } else {
                this.flex.display(.none)
            }
            this.relayoutFlexClimbToXml()
        }
    }
    
    func relayoutFlexClimbToXml() {
        self.flex.markDirty()
        
        func sub(view: UIView){
            if let parent = view.superview, !(parent is UIWindow) {
                sub(view: parent)
            } else {
                view.setNeedsLayout()
            }
        }
        sub(view: self)
    }
    
    enum Animation {
        case push
        case pop
        case fade
    }
    func bindStack(_ dependency: ViewDependency, _ stack: ObservableStack<ViewGenerator>) {
        return bindStack(dependency: dependency, stack: stack)
    }
    func bindStack(dependency: ViewDependency, stack: ObservableStack<ViewGenerator>) {
        var current: UIView? = nil
        var lastCount = 0
        stack.addAndRunWeak(self) { this, value in
            var animation = Animation.fade
            if value.count > lastCount {
                animation = .push
            } else if value.count < lastCount {
                animation = .pop
            }
            lastCount = value.count
            
            if let old = current {
                old.flex.left(0).top(0).right(0).bottom(0)
                old.flex.layout(mode: .fitContainer)
                UIView.animate(
                    withDuration: 0.25,
                    animations: {
                        switch animation {
                        case .fade:
                            old.alpha = 0
                        case .pop:
                            old.flex.left(100%).right(-100%)
                        case .push:
                            old.flex.left(-100%).right(100%)
                        }
                        old.flex.layout(mode: .fitContainer)
                    },
                    completion: { done in
                        old.removeFromSuperview()
                    }
                )
            }
            if let newData = value.last {
                let new = newData.generate(dependency: dependency)
                this.flex.addItem(new).position(.absolute).left(0%).right(0%).top(0%).bottom(0%)
                var startX: CGFloat = 0
                switch animation {
                case .fade:
                    new.alpha = 0
                case .pop:
                    new.flex.left(-100%).right(100%)
                    startX = -this.bounds.size.width
                case .push:
                    new.flex.left(100%).right(-100%)
                    startX = this.bounds.size.width
                }
                new.frame.size = this.bounds.size
                new.frame.origin.x = startX
                new.flex.layout(mode: .fitContainer)
                UIView.animate(
                    withDuration: 0.25,
                    animations: {
                        new.flex.left(0%).right(0%)
                        new.alpha = 1
                        new.frame.size = this.bounds.size
                        new.frame.origin.x = 0
                        new.flex.layout(mode: .fitContainer)
                    }
                )
                current = new
            }
        }
    }
}

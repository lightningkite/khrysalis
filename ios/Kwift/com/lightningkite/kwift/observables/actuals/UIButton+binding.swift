//
//  UIButton+binding.swift
//  KwiftTemplate
//
//  Created by Joseph Ivie on 8/21/19.
//  Copyright © 2019 Joseph Ivie. All rights reserved.
//

import Foundation
import UIKit


public extension UIButton {
    
    func bindActive(_ observable: ObservableProperty<Bool>, _ activeDrawableResource: DrawableResource? = nil, _ inactiveDrawableResource: DrawableResource? = nil) {
        return bindActive(observable: observable, activeDrawableResource: activeDrawableResource, inactiveDrawableResource: inactiveDrawableResource)
    }
    func bindActive(observable: ObservableProperty<Bool>, activeDrawableResource: DrawableResource? = nil, inactiveDrawableResource: DrawableResource? = nil) {
        observable.addAndRunWeak(referenceA: self) { (this, value) in
            this.isUserInteractionEnabled = value
            if value {
                if let background = activeDrawableResource {
                    this.backgroundDrawable = background
                }
            }else{
                if let background = inactiveDrawableResource{
                    this.backgroundDrawable = background
                }
            }
        }
    }

    func bindActive(_ observable: ObservableProperty<Bool>, _ activeColorResource: ColorResource? = nil, _ inactiveColorResource: ColorResource? = nil) {
        return bindActive(observable: observable, activeColorResource: activeColorResource, inactiveColorResource: inactiveColorResource)
    }
    func bindActive(observable: ObservableProperty<Bool>, activeColorResource: ColorResource? = nil, inactiveColorResource: ColorResource? = nil) {
        observable.addAndRunWeak(referenceA: self) { (this, value) in
            this.isUserInteractionEnabled = value
            if value {
                if let color = activeColorResource {
                    this.backgroundColor = color
                }
            }else{
                if let color = inactiveColorResource{
                    this.backgroundColor = color
                }
            }
        }
    }

    func bindBackButton<T>(stack: ObservableStack<T>) {
        stack.onChange.addAndRunWeak(self, stack.stack) { this, value in
            this.isHidden = value.count <= 1
        }
        addAction {
            stack.pop()
        }
    }

    func bindAction(action: @escaping (@escaping ()->Void)->Void) {
        addAction {
            let startText = self.title(for: .normal)
            self.setTitle(nil, for: .normal)
            let activityIndicatorView = UIActivityIndicatorView()
            activityIndicatorView.startAnimating()
            activityIndicatorView.center.x = self.frame.size.width/2
            activityIndicatorView.center.y = self.frame.size.height/2
            self.addSubview(activityIndicatorView)

            weak var weakAIV = activityIndicatorView
            action {
                self.setTitle(startText, for: .normal)
                weakAIV?.stopAnimating()
                weakAIV?.removeFromSuperview()
            }
        }
    }
    
    func bindString(_ observable: ObservableProperty<String>) {
        return bindString(observable: observable)
    }
    func bindString(observable: ObservableProperty<String>) {
        observable.addAndRunWeak(referenceA: self) { (this, value) in
            if this.title(for: .normal) != value {
                this.setTitle(value, for: .normal)
            }
            this.superview?.setNeedsLayout()
        }
    }

    func bindStringRes(_ observableReference: ObservableProperty<StringReference?>) {
        return bindStringRes(observableReference: observableReference)
    }
    func bindStringRes(observableReference: ObservableProperty<StringReference?>) {
        observableReference.addAndRunWeak(referenceA: self) { (this, value) in
            if let value = value {
                if this.title(for: .normal) != value {
                    this.setTitle(value, for: .normal)
                }
            } else {
                this.setTitle("", for: .normal)
            }
            this.superview?.setNeedsLayout()
        }
    }
}

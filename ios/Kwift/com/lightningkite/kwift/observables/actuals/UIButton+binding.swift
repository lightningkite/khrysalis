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

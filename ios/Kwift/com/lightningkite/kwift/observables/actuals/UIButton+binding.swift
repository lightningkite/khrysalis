//
//  UIButton+binding.swift
//  KwiftTemplate
//
//  Created by Joseph Ivie on 8/21/19.
//  Copyright © 2019 Joseph Ivie. All rights reserved.
//

import Foundation
import UIKit


extension UIButton {
    func bindBackButton<T>(stack: ObservableStack<T>) {
        self.titleLabel?.font
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
}

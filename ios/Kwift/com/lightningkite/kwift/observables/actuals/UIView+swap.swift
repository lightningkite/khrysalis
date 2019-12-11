//
//  UIView+swap.swift
//  KwiftTemplate
//
//  Created by Joseph Ivie on 8/21/19.
//  Copyright © 2019 Joseph Ivie. All rights reserved.
//

import Foundation
import UIKit

public extension SwapView {
    func bindStack(_ dependency: ViewDependency, _ stack: ObservableStack<ViewGenerator>) {
        return bindStack(dependency: dependency, stack: stack)
    }
    func bindStack(dependency: ViewDependency, stack: ObservableStack<ViewGenerator>) {
        var lastCount = 0
        stack.addAndRunWeak(self) { this, value in
            DispatchQueue.main.asyncAfter(deadline: .now() + 0.01, execute: {
                var animation = Animation.fade
                if lastCount == 0 {
                    animation = .fade
                } else if value.count > lastCount {
                    animation = .push
                } else if value.count < lastCount {
                    animation = .pop
                }
                lastCount = value.count
                if let newView = value.last?.generate(dependency: dependency) {
                    self.swap(to: newView, animation: animation)
                } else {
                    self.swap(to: nil, animation: animation)
                }                
            })
        }
    }
}

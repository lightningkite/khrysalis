//
//  ViewFlipper+binding.swift
//  Kwift
//
//  Created by Joseph Ivie on 10/24/19.
//  Copyright © 2019 Lightning Kite. All rights reserved.
//

import Foundation
import UIKit


public extension ViewFlipper {
    func bindLoading(_ loading: MutableObservableProperty<Bool>) {
        if subviews.size == 1 {
            let new = UIActivityIndicatorView(frame: .zero)
            new.startAnimating()
            addSubview(new, FrameLayout.LayoutParams(gravity: .center))
        }
        loading.addAndRunWeak(self) { (self, value) in
            self.displayedChild = value ? 1 : 0
        }
    }
    func bindLoading(loading: MutableObservableProperty<Bool>) {
        bindLoading(loading)
    }
}

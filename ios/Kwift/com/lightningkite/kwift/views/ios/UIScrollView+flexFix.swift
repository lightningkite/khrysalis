//
//  UIScrollView+flexFix.swift
//  Lifting Generations
//
//  Created by Joseph Ivie on 6/21/19.
//  Copyright © 2019 Joseph Ivie. All rights reserved.
//

import Foundation
import UIKit


extension UIScrollView {
    public func flexFix(_ sub: UIView){
        let dg = ScrollSavingDelegate()
        delegate = dg
        self.addOnLayoutSubviews { [weak self, weak sub] in
            guard let self = self, let sub = sub else { return }
            self.contentSize = sub.frame.size
            self.contentOffset = dg.lastNonzeroOffset
        }
    }
}

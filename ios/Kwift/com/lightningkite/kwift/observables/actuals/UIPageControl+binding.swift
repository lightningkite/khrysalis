//
//  UIPageContro+binding.swift
//  KwiftTemplate
//
//  Created by Joseph Ivie on 9/4/19.
//  Copyright © 2019 Joseph Ivie. All rights reserved.
//

import Foundation
import UIKit

public extension UIPageControl {
    func bind(_ count: Int32, _ selected: MutableObservableProperty<Int32>) {
        bind(count: count, selected: selected)
    }
    func bind(count: Int32, selected: MutableObservableProperty<Int32>) {
        self.numberOfPages = Int(count)
        var suppress = false
        selected.addAndRunWeak(self) { this, value in
            guard !suppress else { return }
            suppress = true
            this.currentPage = Int(value)
            suppress = false
        }
        self.addAction(for: .valueChanged, action: {
            guard !suppress else { return }
            suppress = true
            selected.value = Int32(self.currentPage)
            suppress = false
        })
    }
}

//
//  UISegmentedControl+bind.swift
//  KwiftTemplate
//
//  Created by Joseph Ivie on 9/4/19.
//  Copyright © 2019 Joseph Ivie. All rights reserved.
//

import Foundation
import UIKit

public extension UISegmentedControl {
    func bind(_ tabs: Array<String>, _ selected: MutableObservableProperty<Int32>) {
        bind(tabs: tabs, selected: selected)
    }
    func bind(tabs: Array<String>, selected: MutableObservableProperty<Int32>) {
        for entry in tabs {
            self.insertSegment(withTitle: entry, at: self.numberOfSegments, animated: false)
        }
        self.addAction(for: .valueChanged, action: { [weak self, weak selected] in
            selected?.value = Int32(self?.selectedSegmentIndex ?? 0)
        })
        selected.addAndRunWeak(self) { this, value in
            this.selectedSegmentIndex = Int(value)
        }
    }
}

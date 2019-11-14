//
//  UIAutoCompleteTextField.swift
//  Kwift
//
//  Created by Joseph Ivie on 11/14/19.
//  Copyright © 2019 Lightning Kite. All rights reserved.
//

import Foundation
import UIKit
import SearchTextField

public extension UIAutoCompleteTextField {
    func bind<T>(
        _ options: ObservableProperty<Array<T>>,
        _ toString: @escaping (T) -> String,
        _ onItemSelected: @escaping (T) -> Void
    ) {
        bind(options: options, toString: toString, onItemSelected: onItemSelected)
    }
    func bind<T>(
        options: ObservableProperty<Array<T>>,
        toString: @escaping (T) -> String,
        onItemSelected: @escaping (T) -> Void
    ) {
        if let font = font { theme.font = font }
        if let textColor = textColor { theme.fontColor = textColor }
        
        var optionsMap = Dictionary<String, T>()
        options.addAndRunWeak(self) { (self, value) in
            optionsMap = [:]
            for item in value {
                let original = toString(item)
                var asString = original
                var index = 2
                while optionsMap[asString] != nil {
                    asString = original + " (\(index))"
                    index += 1
                }
                optionsMap[asString] = item
            }
            let array = Array(optionsMap.keys)
            self.filterStrings(array)
        }
        itemSelectionHandler = { (items, itemPosition) in
            if let item = optionsMap[items[itemPosition].title] {
                onItemSelected(item)
            }
        }
    }
}

//
//  UITextView+binding.swift
//  KwiftTemplate
//
//  Created by Joseph Ivie on 8/21/19.
//  Copyright © 2019 Joseph Ivie. All rights reserved.
//

import Foundation
import UIKit


public extension UITextView {

    class LambdaDelegate: NSObject, UITextViewDelegate {
        let action: (String) -> Void

        init(action: @escaping (String) -> Void) {
            self.action = action
            super.init()
        }

        public func textViewDidChange(_ textView: UITextView) {
            action(textView.text)
        }
    }

    func bindEditable(observable: MutableObservableProperty<String>) {
        observable.addAndRunWeak(referenceA: self) { (this, value) in
            if this.text != value {
                this.text = value
            }
            this.flex.markDirty()
        }
        weak var observableWeak = observable
        let delegate = LambdaDelegate { text in
            if observableWeak?.value != text {
                observableWeak?.value = text
            }
        }
        retain(as: "kwift_dg", item: delegate)
        self.delegate = delegate
    }

    func bind(observable: ObservableProperty<String>) {
        isEditable = false
        observable.addAndRunWeak(referenceA: self) { (this, value) in
            if this.text != value {
                this.text = value
            }
            this.flex.markDirty()
            this.relayoutFlexClimbToXml()
        }
    }

    func bind(observableReference: ObservableProperty<StringReference?>) {
        isEditable = false
        observableReference.addAndRunWeak(referenceA: self) { (this, value) in
            if let value = value {
                let localValue = NSLocalizedString(value, comment: "")
                if this.text != localValue {
                    this.text = localValue
                }
            } else {
                this.text = nil
            }
            this.flex.markDirty()
            this.relayoutFlexClimbToXml()
        }
    }
}

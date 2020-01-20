//Stub file made with Kwift 2 (by Lightning Kite)
import Foundation
import UIKit


//--- EditText.bindString(MutableObservableProperty<String>)
public extension UITextField {
    func bindString(_ observable: MutableObservableProperty<String>) -> Void {
        delegate = DoneDelegate.shared
        observable.addAndRunWeak(referenceA: self) { (this, value) in
            if this.textString != value {
                this.textString = value
            }
            this.superview?.setNeedsLayout()
        }
        addAction(for: UITextField.Event.editingChanged) { [weak self] in
            if observable.value != self?.textString {
                observable.value = self?.textString ?? ""
            }
        }
    }
    func bindString(observable: MutableObservableProperty<String>) -> Void {
        return bindString(observable)
    }
}
public extension UITextView {
    class LambdaDelegate: NSObject, UITextViewDelegate {
        let action: (String) -> Void

        init(action: @escaping (String) -> Void) {
            self.action = action
            super.init()
        }

        public func textViewDidChange(_ textView: UITextView) {
            action(textView.textString)
        }
    }
    func bindString(_ observable: MutableObservableProperty<String>) -> Void {
        observable.addAndRunWeak(referenceA: self) { (this, value) in
            if this.textString != value {
                this.textString = value
            }
            this.superview?.setNeedsLayout()
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
    func bindString(observable: MutableObservableProperty<String>) -> Void {
        return bindString(observable)
    }
}

//--- EditText.bindInteger(MutableObservableProperty<Int>)
public extension UITextField {
    func bindInteger(_ observable: MutableObservableProperty<Int32>) -> Void {
        delegate = DoneDelegate.shared
        if observable.value != 0 {
            text = observable.value.toString()
        }
        observable.addAndRunWeak(referenceA: self) { (this, value) in
            let currentValue = Int(this.textString)
            if currentValue != nil, currentValue != Int(value) {
                this.textString = value.toString()
                this.superview?.setNeedsLayout()
            }
        }
        addAction(for: UITextField.Event.editingChanged) { [weak self] in
            if let self = self, let currentValue = Int(self.textString), observable.value != currentValue {
                observable.value = Int32(currentValue)
            }
        }
    }
    func bindInteger(observable: MutableObservableProperty<Int32>) -> Void {
        return bindInteger(observable)
    }
}

//--- EditText.bindDouble(MutableObservableProperty<Double>)
public extension UITextField {
    func bindDouble(_ observable: MutableObservableProperty<Double>) -> Void {
        delegate = DoneDelegate.shared
        if observable.value != 0.0 {
            text = observable.value.toString()
        }
        observable.addAndRunWeak(referenceA: self) { (this, value) in
            let currentValue = Double(this.textString)
            if currentValue != nil, currentValue != value {
                this.textString = value.toString()
                this.superview?.setNeedsLayout()
            }
        }
        addAction(for: UITextField.Event.editingChanged) { [weak self] in
            if let self = self, let currentValue = Double(self.textString), observable.value != currentValue {
                observable.value = currentValue
            }
        }
    }
    func bindDouble(observable: MutableObservableProperty<Double>) -> Void {
        return bindDouble(observable)
    }
}


















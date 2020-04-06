//Stub file made with Khrysalis 2 (by Lightning Kite)
import Foundation
import UIKit


//--- EditText.bindString(MutableObservableProperty<String>)
public extension UITextField {
    func bindString(_ observable: MutableObservableProperty<String>) -> Void {
        delegate = DoneDelegate.shared
        observable.subscribeBy { ( value) in
            if self.textString != value {
                self.textString = value
            }
            self.superview?.setNeedsLayout()
        }.until(self.removed)
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
        observable.subscribeBy { ( value) in
            if self.textString != value {
                self.textString = value
            }
            self.superview?.setNeedsLayout()
        }.until(self.removed)
        weak var observableWeak = observable
        let delegate = LambdaDelegate { text in
            if observableWeak?.value != text {
                observableWeak?.value = text
            }
        }
        retain(as: "khrysalis_dg", item: delegate)
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
        observable.subscribeBy { ( value) in
            let currentValue = Int(self.textString) ?? 0
            if currentValue != Int(value) {
                self.textString = value.toString()
                self.superview?.setNeedsLayout()
            }
        }.until(self.removed)
        addAction(for: UITextField.Event.editingChanged) { [weak self] in
            if let self = self {
                let currentValue = Int(self.textString) ?? 0
                if observable.value != currentValue {
                    observable.value = Int32(currentValue)
                }
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
        observable.subscribeBy { ( value) in
            let currentValue = Double(self.textString) ?? 0
            if currentValue != Double(value) {
                self.textString = value.toString()
                self.superview?.setNeedsLayout()
            }
        }.until(self.removed)
        addAction(for: UITextField.Event.editingChanged) { [weak self] in
            if let self = self {
                let currentValue = Double(self.textString) ?? 0
                if observable.value != currentValue {
                    observable.value = Double(currentValue)
                }
            }
        }
    }
    func bindDouble(observable: MutableObservableProperty<Double>) -> Void {
        return bindDouble(observable)
    }
}

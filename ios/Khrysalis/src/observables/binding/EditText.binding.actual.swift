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
            self.notifyParentSizeChanged()
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
            self.notifyParentSizeChanged()
        }.until(self.removed)
        let delegate = LambdaDelegate { text in
            if observable.value != text {
                observable.value = text
            }
        }
        retain(as: "khrysalis_dg", item: delegate, until: removed)
        self.delegate = delegate
    }
    func bindString(observable: MutableObservableProperty<String>) -> Void {
        return bindString(observable)
    }
}

//--- EditText.bindInteger(MutableObservableProperty<Int>)
public extension UITextField {
    func bindInteger(_ observable: MutableObservableProperty<Int>) -> Void {
        delegate = DoneDelegate.shared
        observable.subscribeBy { ( value) in
            let currentValue = Int(self.textString) ?? 0
            if currentValue != Int(value) {
                self.textString = String(value)
                self.notifyParentSizeChanged()
            }
        }.until(self.removed)
        addAction(for: UITextField.Event.editingChanged) { [weak self] in
            if let self = self {
                let currentValue = Int(self.textString) ?? 0
                if observable.value != currentValue {
                    observable.value = Int(currentValue)
                }
            }
        }
    }
    func bindInteger(observable: MutableObservableProperty<Int>) -> Void {
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
                self.textString = String(value)
                self.notifyParentSizeChanged()
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

//Stub file made with Khrysalis 2 (by Lightning Kite)
import Foundation
import UIKit


//--- TextView.bindString(ObservableProperty<String>)
public extension UILabel {
    func bindString(_ observable: ObservableProperty<String>) -> Void {
        observable.subscribeBy { ( value) in
            if self.textString != value {
                self.textString = value
            }
            self.notifyParentSizeChanged()
        }.until(self.removed)
    }
    func bindString(observable: ObservableProperty<String>) -> Void {
        return bindString(observable)
    }
}
public extension UITextView {
    func bindString(_ observable: ObservableProperty<String>) {
        return bindString(observable: observable)
    }
    func bindString(observable: ObservableProperty<String>) {
        observable.subscribeBy { ( value) in
            if self.textString != value {
                self.textString = value
            }
            self.notifyParentSizeChanged()
        }.until(self.removed)
    }
}
public extension UITextField {
    func bindString(_ observable: ObservableProperty<String>) {
        return bindString(observable: observable)
    }
    func bindString(observable: ObservableProperty<String>) {
        observable.subscribeBy { ( value) in
            if self.textString != value {
                self.textString = value
            }
            self.notifyParentSizeChanged()
        }.until(self.removed)
    }
}
public extension UIButton {
    func bindString(_ observable: ObservableProperty<String>) {
        return bindString(observable: observable)
    }
    func bindString(observable: ObservableProperty<String>) {
        observable.subscribeBy { ( value) in
            if self.title(for: .normal) != value {
                self.textString = value
            }
            self.notifyParentSizeChanged()
        }.until(self.removed)
    }
}

//--- TextView.bindStringRes(ObservableProperty<StringResource?>)
public extension UILabel {
    func bindStringRes(_ observable: ObservableProperty<StringResource?>) -> Void {
        observable.subscribeBy { ( value) in
            if let value = value {
                let localValue = NSLocalizedString(value, comment: "")
                if self.textString != localValue {
                    self.textString = localValue
                }
            } else {
                self.text = nil
            }
            self.notifyParentSizeChanged()
        }.until(self.removed)
    }
    func bindStringRes(observable: ObservableProperty<StringResource?>) -> Void {
        return bindStringRes(observable)
    }
}
public extension UITextView {
    func bindStringRes(_ observableReference: ObservableProperty<StringResource?>) {
        return bindStringRes(observableReference: observableReference)
    }
    func bindStringRes(observableReference: ObservableProperty<StringResource?>) {
        observableReference.subscribeBy { ( value) in
            if let value = value {
                let localValue = NSLocalizedString(value, comment: "")
                if self.textString != localValue {
                    self.textString = localValue
                }
            } else {
                self.text = nil
            }
            self.notifyParentSizeChanged()
        }.until(self.removed)
    }
}
public extension UITextField {
    func bindStringRes(_ observableReference: ObservableProperty<StringResource?>) {
        return bindStringRes(observableReference: observableReference)
    }
    func bindStringRes(observableReference: ObservableProperty<StringResource?>) {
        observableReference.subscribeBy { ( value) in
            if let value = value {
                let localValue = NSLocalizedString(value, comment: "")
                if self.textString != localValue {
                    self.textString = localValue
                }
            } else {
                self.text = nil
            }
            self.notifyParentSizeChanged()
        }.until(self.removed)
    }
}
public extension UIButton {
    func bindStringRes(_ observableReference: ObservableProperty<StringResource?>) {
        return bindStringRes(observableReference: observableReference)
    }
    func bindStringRes(observableReference: ObservableProperty<StringResource?>) {
        observableReference.subscribeBy { ( value) in
            if let value = value {
                if self.title(for: .normal) != value {
                    self.textString = value
                }
            } else {
                self.textString = ""
            }
            self.notifyParentSizeChanged()
        }.until(self.removed)
    }
}

//--- TextView.bindText(ObservableProperty<T>, (T)->String)
public extension UILabel {
    func bindText<T>(_ observable: ObservableProperty<T>, _ transform: @escaping (T) -> String) -> Void {
        observable.subscribeBy { ( value) in
            let textValue = transform(value)
            if self.textString != textValue {
                self.textString = textValue
            }
            self.notifyParentSizeChanged()
        }.until(self.removed)
    }
    func bindText<T>(observable: ObservableProperty<T>, transform: @escaping (T) -> String) -> Void {
        return bindText(observable, transform)
    }
}

public extension HasLabelView where Self: UIView {
    func bindString(_ observable: ObservableProperty<String>) -> Void {
        observable.subscribeBy { ( value) in
            if self.textString != value {
                self.textString = value
            }
            self.notifyParentSizeChanged()
        }.until(self.removed)
    }
    func bindString(observable: ObservableProperty<String>) -> Void {
        return bindString(observable)
    }
}

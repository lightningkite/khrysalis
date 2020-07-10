//Stub file made with Khrysalis 2 (by Lightning Kite)
import Foundation
import UIKit


//--- View.bindVisible(ObservableProperty<Boolean>)
public extension UIView {
    func bindVisible(_ observable: ObservableProperty<Bool>) -> Void {
        observable.subscribeBy { ( value) in
            self.isHidden = !value
        }.until(self.removed)
    }
    func bindVisible(observable: ObservableProperty<Bool>) -> Void {
        return bindVisible(observable)
    }
}

//--- View.bindExists(ObservableProperty<Boolean>)
public extension UIView {
    func bindExists(_ observable: ObservableProperty<Bool>) -> Void {
        observable.subscribeBy { ( value) in
            self.includeInLayout = value
            self.isHidden = !value
        }.until(self.removed)
    }
    func bindExists(observable: ObservableProperty<Bool>) -> Void {
        return bindExists(observable)
    }
}

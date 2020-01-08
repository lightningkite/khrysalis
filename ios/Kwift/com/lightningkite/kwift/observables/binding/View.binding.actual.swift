//Stub file made with Kwift 2 (by Lightning Kite)
import Foundation
import UIKit


//--- View.bindVisible(ObservableProperty<Boolean>)
public extension UIView {
    func bindVisible(_ observable: ObservableProperty<Bool>) -> Void {
        observable.addAndRunWeak(referenceA: self) { (this, value) in
            this.isHidden = !value
        }
    }
    func bindVisible(observable: ObservableProperty<Bool>) -> Void {
        return bindVisible(observable)
    }
}

//--- View.bindExists(ObservableProperty<Boolean>)
public extension UIView {
    func bindExists(_ observable: ObservableProperty<Bool>) -> Void {
        observable.addAndRunWeak(referenceA: self) { (this, value) in
            this.includeInLayout = value
            this.isHidden = !value
        }
    }
    func bindExists(observable: ObservableProperty<Bool>) -> Void {
        return bindExists(observable)
    }
}










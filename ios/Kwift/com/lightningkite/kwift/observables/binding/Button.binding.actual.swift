//Stub file made with Kwift 2 (by Lightning Kite)
import Foundation
import UIKit


//--- Button.bindActive(ObservableProperty<Boolean>, ColorResource? , ColorResource? )
public extension UIButton {
    func bindActive(_ observable: ObservableProperty<Bool>, _ activeColorResource: ColorResource? = nil, _ inactiveColorResource: ColorResource? = nil) -> Void {
        observable.addAndRunWeak(referenceA: self) { (this, value) in
            this.isUserInteractionEnabled = value
            if value {
                if let color = activeColorResource {
                    this.backgroundColor = color
                }
            }else{
                if let color = inactiveColorResource{
                    this.backgroundColor = color
                }
            }
        }
    }
    func bindActive(observable: ObservableProperty<Bool>, activeColorResource: ColorResource? = nil, inactiveColorResource: ColorResource? = nil) -> Void {
        return bindActive(observable, activeColorResource, inactiveColorResource)
    }
}





//--- Button.bindActive(ObservableProperty<Boolean>, Drawable, Drawable)
public extension UIButton {
    func bindActive(_ observable: ObservableProperty<Bool>, _ activeBackground: @escaping Drawable, _ inactiveBackground: @escaping Drawable) -> Void {
        observable.addAndRunWeak(referenceA: self) { (this, value) in
            this.isUserInteractionEnabled = value
            if value {
                this.backgroundDrawable = activeBackground
            }else{
                this.backgroundDrawable = inactiveBackground
            }
        }
    }
    func bindActive(observable: ObservableProperty<Bool>, activeBackground: @escaping Drawable, inactiveBackground: @escaping Drawable) -> Void {
        return bindActive(observable, activeBackground, inactiveBackground)
    }
}






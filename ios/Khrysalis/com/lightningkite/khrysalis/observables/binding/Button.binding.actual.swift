//Stub file made with Khrysalis 2 (by Lightning Kite)
import Foundation
import UIKit


//--- Button.bindActive(ObservableProperty<Boolean>, ColorResource? , ColorResource? )
public extension UIButton {
    func bindActive(_ observable: ObservableProperty<Bool>, _ activeColorResource: ColorResource? = nil, _ inactiveColorResource: ColorResource? = nil) -> Void {
        observable.subscribeBy { ( value) in
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
        }.until(self.removed)
    }
    func bindActive(observable: ObservableProperty<Bool>, activeColorResource: ColorResource? = nil, inactiveColorResource: ColorResource? = nil) -> Void {
        return bindActive(observable, activeColorResource, inactiveColorResource)
    }
}





//--- Button.bindActive(ObservableProperty<Boolean>, Drawable, Drawable)
public extension UIButton {
    func bindActive(_ observable: ObservableProperty<Bool>, _ activeBackground: @escaping Drawable, _ inactiveBackground: @escaping Drawable) -> Void {
        observable.subscribeBy { ( value) in
            this.isUserInteractionEnabled = value
            if value {
                this.backgroundDrawable = activeBackground
            }else{
                this.backgroundDrawable = inactiveBackground
            }
        }.until(self.removed)
    }
    func bindActive(observable: ObservableProperty<Bool>, activeBackground: @escaping Drawable, inactiveBackground: @escaping Drawable) -> Void {
        return bindActive(observable, activeBackground, inactiveBackground)
    }
}

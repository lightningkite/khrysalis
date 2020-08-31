//Stub file made with Khrysalis 2 (by Lightning Kite)
import Foundation
import UIKit


//--- Button.bindActive(ObservableProperty<Boolean>, ColorResource? , ColorResource? )
public extension UIButton {
    func bindActive(_ observable: ObservableProperty<Bool>, _ activeColorResource: ColorResource? = nil, _ inactiveColorResource: ColorResource? = nil) -> Void {
        observable.subscribeBy { ( value) in
            self.isUserInteractionEnabled = value
            if value {
                if let color = activeColorResource {
                    self.backgroundColor = color
                }
            }else{
                if let color = inactiveColorResource{
                    self.backgroundColor = color
                }
            }
        }.until(self.removed)
    }
    func bindActive(observable: ObservableProperty<Bool>, activeColorResource: ColorResource? = nil, inactiveColorResource: ColorResource? = nil) -> Void {
        return bindActive(observable, activeColorResource, inactiveColorResource)
    }
    
    
    func bindActive(_ observable: ObservableProperty<Bool>, _ activeColorResource: DrawableResource? = nil, _ inactiveColorResource: DrawableResource? = nil) -> Void {
        observable.subscribeBy { ( value) in
            self.isUserInteractionEnabled = value
            if value {
                if let drawable = activeColorResource {
                    self.backgroundDrawable = drawable
                }
            }else{
                if let drawable = inactiveColorResource{
                    self.backgroundDrawable = drawable
                }
            }
        }.until(self.removed)
    }
    func bindActive(observable: ObservableProperty<Bool>, activeColorResource: DrawableResource? = nil, inactiveColorResource: DrawableResource? = nil) -> Void {
        return bindActive(observable, activeColorResource, inactiveColorResource)
    }
}





//--- Button.bindActive(ObservableProperty<Boolean>, Drawable, Drawable)
public extension UIButton {
    func bindActive(_ observable: ObservableProperty<Bool>, _ activeBackground: Drawable, _ inactiveBackground: Drawable) -> Void {
        observable.subscribeBy { ( value) in
            self.isUserInteractionEnabled = value
            if value {
                self.backgroundDrawable = activeBackground
            }else{
                self.backgroundDrawable = inactiveBackground
            }
        }.until(self.removed)
    }
    func bindActive(observable: ObservableProperty<Bool>, activeBackground: Drawable, inactiveBackground: Drawable) -> Void {
        return bindActive(observable, activeBackground, inactiveBackground)
    }
}

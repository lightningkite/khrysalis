//Stub file made with Kwift 2 (by Lightning Kite)
import Foundation


//--- Button.compoundDrawable
public extension UIButtonWithLayer {
    var compoundDrawable: Drawable? {
        get {
            if let iconLayer = iconLayer {
                return { _ in iconLayer }
            } else {
                return nil
            }
        }
        set(value) {
            if let value = value {
                iconLayer = value(self)
            } else {
                iconLayer = nil
            }
        }
    }
    
    func setImageResource(_ image: @escaping DrawableResource ) {
        self.compoundDrawable = image
        self.superview?.setNeedsLayout()
    }
}










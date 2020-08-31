//Stub file made with Khrysalis 2 (by Lightning Kite)
import Foundation


//--- Button.compoundDrawable
public extension UIButtonWithLayer {
    var compoundDrawable: Drawable? {
        get {
            if let iconLayer = iconLayer {
                return Drawable { _ in iconLayer }
            } else {
                return nil
            }
        }
        set(value) {
            if let value = value {
                iconLayer = value.makeLayer(self)
            } else {
                iconLayer = nil
            }
        }
    }
    
    func setImageResource(_ image: DrawableResource ) {
        self.compoundDrawable = image
        self.notifyParentSizeChanged()
    }
}

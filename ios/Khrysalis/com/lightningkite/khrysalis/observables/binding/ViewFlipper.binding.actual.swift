//Stub file made with Khrysalis 2 (by Lightning Kite)
import Foundation
import UIKit


//--- ViewFlipper.bindLoading(MutableObservableProperty<Boolean>)
public extension ViewFlipper {
    func bindLoading(_ loading: MutableObservableProperty<Bool>, _ color: ColorResource? = null) -> Void {
        if subviews.size == 1 {
            let new = UIActivityIndicatorView(frame: .zero)
            if let color = color {
                new.color = color
            }
            new.startAnimating()
            addSubview(new, FrameLayout.LayoutParams(gravity: .center))
        }
        loading.addAndRunWeak(self) { (self, value) in
            self.displayedChild = value ? 1 : 0
        }
    }
    func bindLoading(loading: MutableObservableProperty<Bool>, color: ColorResource? = null) -> Void {
        return bindLoading(loading)
    }
}

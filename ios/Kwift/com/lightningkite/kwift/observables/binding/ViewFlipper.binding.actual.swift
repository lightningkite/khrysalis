//Stub file made with Kwift 2 (by Lightning Kite)
import Foundation
import UIKit


//--- ViewFlipper.bindLoading(MutableObservableProperty<Boolean>)
public extension ViewFlipper {
    func bindLoading(_ loading: MutableObservableProperty<Bool>) -> Void {
        if subviews.size == 1 {
            let new = UIActivityIndicatorView(frame: .zero)
            new.startAnimating()
            addSubview(new, FrameLayout.LayoutParams(gravity: .center))
        }
        loading.addAndRunWeak(self) { (self, value) in
            self.displayedChild = value ? 1 : 0
        }
    }
    func bindLoading(loading: MutableObservableProperty<Bool>) -> Void {
        return bindLoading(loading)
    }
}

















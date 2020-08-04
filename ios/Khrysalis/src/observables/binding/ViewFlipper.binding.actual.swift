//Stub file made with Khrysalis 2 (by Lightning Kite)
import Foundation
import UIKit


//--- ViewFlipper.bindLoading(ObservableProperty<Boolean>, ColorResource? )
public extension ViewFlipper {
    func bindLoading(_ loading: ObservableProperty<Bool>, _ color: ColorResource? = nil) -> Void {
        if subviews.count == 1 {
            let new = UIActivityIndicatorView(frame: .zero)
            if let color = color ?? ViewFlipper.defaultLoadingColor ?? UIView.appAccentColor {
                new.color = color
            }
            new.startAnimating()
            addSubview(new, FrameLayout.LayoutParams(gravity: .center))
        }
        loading.subscribeBy { (value) in
            self.displayedChild = value ? 1 : 0
        }.until(self.removed)
    }
    func bindLoading(loading: ObservableProperty<Bool>, color: ColorResource? = nil) -> Void {
        return bindLoading(loading)
    }
    static var defaultLoadingColor: UIColor? = nil
}

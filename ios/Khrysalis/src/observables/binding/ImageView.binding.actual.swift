//Stub file made with Khrysalis 2 (by Lightning Kite)
import Foundation
import AlamofireImage
import Alamofire


//--- ImageView.loadImage(Image?)

//--- ImageView.bindImage(ObservableProperty<Image?>)
public extension UIImageView {
    func bindImage(_ image: ObservableProperty<Image?>) -> Void {
        image.subscribeBy { it in
            self.loadImage(it)
        }.until(self.removed)
    }
    func bindImage(image: ObservableProperty<Image?>) -> Void {
        return bindImage(image)
    }
}

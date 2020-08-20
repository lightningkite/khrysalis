//Stub file made with Khrysalis 2 (by Lightning Kite)
import Foundation
import UIKit


//--- Bitmap
public typealias Bitmap = UIImage

//--- loadImage(Image, (Bitmap?)->Unit)
public func loadImage(_ image: Image, _ onResult: @escaping (Bitmap?) -> Void) -> Void {
    image.load().subscribe(
        onSuccess: { onResult($0) },
        onError: { onResult(nil) }
    )
}
public func loadImage(image: Image, onResult: @escaping (Bitmap?) -> Void) -> Void {
    return loadImage(image, onResult)
}

//--- Image.load((Bitmap?)->Unit)
public extension Image {
    func load(_ onResult: @escaping (Bitmap?) -> Void) -> Void {
        return loadImage(self, onResult)
    }
}

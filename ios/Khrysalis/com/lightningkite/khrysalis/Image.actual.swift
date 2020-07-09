//Stub file made with Khrysalis 2 (by Lightning Kite)
import Foundation
import UIKit


//--- Bitmap
public typealias Bitmap = UIImage

//--- loadImage(Image, (Bitmap?)->Unit)
public func loadImage(_ image: Image, _ onResult: @escaping (Bitmap?) -> Void) -> Void {
    switch(image){
    case let image as ImageReference:
        loadImage(uri: image.uri, onResult: onResult)
    case let image as ImageBitmap:
        onResult(image.bitmap)
    case let image as ImageRaw:
        onResult(UIImage(data: image.raw))
    case let image as ImageRemoteUrl:
        loadImage(image.url, onResult)
    default:
        onResult(nil)
    }
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

//--- loadImage(Uri, Int, (Bitmap?)->Unit)
public func loadImage(_ uri: URL, _ maxDimension: Int32 = 2048, _ onResult: @escaping (Bitmap?) -> Void) -> Void {
    URLSession.shared.dataTask(with: uri, completionHandler: { data, response, error in
        DispatchQueue.main.async {
            if let data = data {
                onResult(UIImage(data: data))
            } else {
                onResult(nil)
            }
        }
    }).resume()
}
public func loadImage(uri: URL, maxDimension: Int32 = 2048, onResult: @escaping (Bitmap?) -> Void) -> Void {
    return loadImage(uri, maxDimension, onResult)
}

//--- loadImage(String, (Bitmap?)->Unit)
public func loadImage(_ url: String, _ onResult: @escaping (Bitmap?) -> Void) -> Void {
    URLSession.shared.dataTask(with: URL(string: url)!, completionHandler: { data, response, error in
        DispatchQueue.main.async {
            if let data = data {
                onResult(UIImage(data: data))
            } else {
                onResult(nil)
            }
        }
    }).resume()
}
public func loadImage(url: String, onResult: @escaping (Bitmap?) -> Void) -> Void {
    return loadImage(url, onResult)
}

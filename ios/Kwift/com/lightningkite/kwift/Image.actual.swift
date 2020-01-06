//Stub file made with Kwift 2 (by Lightning Kite)
import Foundation


//--- Bitmap
public typealias Bitmap = UIImage

//--- loadImage(Image, (Bitmap?)->Unit)
public func loadImage(_ image: Image, _ onResult: @escaping (Bitmap?) -> Void) -> Void {
    switch(image){
    case let image as ImageReference:
        loadImage(image.uri, onResult)
    case let image as ImageBitmap:
        onResult(image.bitmap)
    case let image as ImageRaw:
        onResult(nil)
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

//--- loadImage(Uri, (Bitmap?)->Unit)
public func loadImage(_ uri: Uri, _ onResult: @escaping (Bitmap?) -> Void) -> Void {
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
public func loadImage(uri: Uri, onResult: @escaping (Bitmap?) -> Void) -> Void {
    return loadImage(uri, onResult)
}

//--- loadImage(String, (Bitmap?)->Unit)
public func loadImage(_ url: String, _ onResult: @escaping (Bitmap?) -> Void) -> Void {
    URLSession.shared.dataTask(with: Uri(string: url)!, completionHandler: { data, response, error in
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








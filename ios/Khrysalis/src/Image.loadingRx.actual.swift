
import Foundation
import RxSwift

//--- Image.load()
public extension Image {
    func load() -> Single<Bitmap> {
        switch self {
        case let self as ImageRaw:
            if let image = UIImage(data: self.raw) {
                return Single.just(image)
            } else {
                return Single.error(IllegalArgumentException("Could not parse image"))
            }
        case let self as ImageReference:
            return HttpClient.call(url: self.uri.absoluteString).unsuccessfulAsError().map { UIImage(data: $0.data)! }
        case let self as ImageBitmap:
            return Single.just(self.bitmap)
        case let self as ImageRemoteUrl:
            return HttpClient.call(url: self.url).unsuccessfulAsError().map { UIImage(data: $0.data)! }
        case let self as ImageResource:
            let res: DrawableResource = self.resource
            let layer = res(nil)
            if let img = layer.toImage() {
                return Single.just(img)
            }
            return Single.error(IllegalArgumentException("Could not convert resource to image."))
        default:
            return Single.error(IllegalArgumentException("Unknown image type \(self)"))
        }
    }
}

//--- ImageReference.load(Int)
public extension ImageReference {
    func load(_ maxDimension: Int32) -> Single<Bitmap> {
        return HttpClient.call(url: self.uri.absoluteString).unsuccessfulAsError().map { UIImage(data: $0.data)! }
    }
    func load(maxDimension: Int32) -> Single<Bitmap> {
        return load(maxDimension)
    }
}

//--- ImageRemoteUrl.load()

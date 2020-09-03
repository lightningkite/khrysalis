import AVKit
import UIKit
import RxSwift

public extension Video {
    func thumbnail(timeMs: Int64 = 2000, size: CGPoint? = nil) -> Single<Image> {
        return Single.create { (em: SingleEmitter<Image>) in
            let vid: AVAsset
            switch self {
            case let self as VideoRemoteUrl:
                guard let url = URL(string: self.url) else {
                    em.onError(IllegalArgumentException("Invalid video URL \(self.url)", nil))
                    return
                }
                vid = AVAsset(url: url)
            case let self as VideoReference:
                vid = AVAsset(url: self.uri)
            default:
                em.onError(IllegalArgumentException("Invalid video type \(self)", nil))
                return
            }
            let imageGenerator = AVAssetImageGenerator(asset: vid)
            imageGenerator.appliesPreferredTrackTransform = true
            let time = CMTime(seconds: Double(timeMs) / 1000.0, preferredTimescale: 600)
            let times = [NSValue(time: time)]
            imageGenerator.generateCGImagesAsynchronously(forTimes: times, completionHandler: { _, image, _, _, _ in
                if let image = image {
                    em.onSuccess(ImageBitmap(bitmap: UIImage(cgImage: image)))
                } else {
                    em.onError(IllegalStateException("Could not generate preview", nil))
                }
            })
        }.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
    }
}



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
            return loadUrl(url: self.uri).map { UIImage(data: $0.second)! }
        case let self as ImageBitmap:
            return Single.just(self.bitmap)
        case let self as ImageRemoteUrl:
            if let url = URL(string: self.url) {
                return loadUrl(url: url).map { UIImage(data: $0.second)! }
            } else {
                return Single.error(IllegalArgumentException("Invalid URL \(self.url)"))
            }
        default:
            return Single.error(IllegalArgumentException("Unknown image type \(self)"))
        }
    }
}

private func loadUrl(url: URL) -> Single<Pair<String, Data>> {
    return Single.create { (em) in
        URLSession.shared.dataTask(with: url, completionHandler: { data, response, error in
            DispatchQueue.main.async {
                if let response = response as? HTTPURLResponse {
                    if response.statusCode / 100 == 2, let data = data {
                        let mediaType = response.mimeType ?? "application/octet-stream"
                        em.onSuccess(Pair(first: mediaType, second: data))
                    } else if let error = error {
                        em.onError(error)
                    } else {
                        em.onError(HttpResponseException(response: HttpResponse(response: response, data: data ?? Data())))
                    }
                } else if let response = response {
                    if let data = data {
                        let mediaType = response.mimeType ?? "application/octet-stream"
                        em.onSuccess(Pair(first: mediaType, second: data))
                    } else if let error = error {
                        em.onError(error)
                    } else {
                        em.onError(IllegalStateException("Conversion to HttpBody failed for an unknown reason"))
                    }
                } else {
                    em.onError(IllegalStateException("Conversion to HttpBody failed for an unknown reason"))
                }
            }
        }).resume()
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

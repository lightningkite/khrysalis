import Foundation
import RxSwift

//--- HttpBody
public struct HttpBody {
    public let mediaType: String
    public let data: Data
    public init(mediaType: String, data: Data){
        self.mediaType = mediaType
        self.data = data
    }
}

//--- HttpBodyPart
public enum HttpBodyPart {
    case file(name: String, filename: String?, body: HttpBody)
    case value(name: String, value: String)
}

//--- IsCodable.toJsonHttpBody()
public extension Encodable {
    func toJsonHttpBody() -> HttpBody {
        return HttpBody(mediaType: HttpMediaTypes.JSON, data: self.toJsonData())
    }
}
public func xAnyToJsonHttpBody(_ value: Encodable) -> HttpBody {
    return HttpBody(mediaType: HttpMediaTypes.JSON, data: xAnyToJsonData(value))
}
public func xAnyToJsonHttpBody(_ value: Dictionary<String, Any>) -> HttpBody {
    return HttpBody(mediaType: HttpMediaTypes.JSON, data: xAnyToJsonData(value))
}
public func xAnyToJsonHttpBody(_ value: Dictionary<String, Any?>) -> HttpBody {
    return HttpBody(mediaType: HttpMediaTypes.JSON, data: xAnyToJsonData(value))
}
public func xAnyToJsonHttpBody(_ value: Dictionary<String, Codable>) -> HttpBody {
    return HttpBody(mediaType: HttpMediaTypes.JSON, data: xAnyToJsonData(value))
}
public func xAnyToJsonHttpBody(_ value: Dictionary<String, Codable?>) -> HttpBody {
    return HttpBody(mediaType: HttpMediaTypes.JSON, data: xAnyToJsonData(value))
}

//--- Data.toHttpBody(HttpMediaType)
public extension Data {
    func toHttpBody(_ mediaType: HttpMediaType) -> HttpBody {
        return HttpBody(mediaType: mediaType, data: self)
    }
    func toHttpBody(mediaType: HttpMediaType) -> HttpBody {
        return toHttpBody(mediaType)
    }
}

//--- String.toHttpBody(HttpMediaType)
public extension String {
    func toHttpBody(_ mediaType: HttpMediaType) -> HttpBody {
        return HttpBody(mediaType: mediaType, data: self.data(using: .utf8)!)
    }
    func toHttpBody(mediaType: HttpMediaType) -> HttpBody {
        return toHttpBody(mediaType)
    }
}

//--- Uri.toHttpBody()
public extension URL {
    func toHttpBody() -> Single<HttpBody> {
        return Single.create { (em) in
            URLSession.shared.dataTask(with: self, completionHandler: { data, response, error in
                DispatchQueue.main.async {
                    if let response = response as? HTTPURLResponse {
                        if response.statusCode / 100 == 2, let data = data {
                            let mediaType = response.mimeType ?? "application/octet-stream"
                            em.onSuccess(HttpBody(mediaType: mediaType, data: data))
                        } else if let error = error {
                            em.onError(error)
                        } else {
                            em.onError(HttpResponseException(response: HttpResponse(response: response, data: data ?? Data())))
                        }
                    } else if let response = response {
                        if let data = data {
                            let mediaType = response.mimeType ?? "application/octet-stream"
                            em.onSuccess(HttpBody(mediaType: mediaType, data: data))
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
}

//--- Image.toHttpBody
fileprivate extension UIImage {
    func resize(maxDimension: Int) -> UIImage? {
        var newSize = CGSize.zero
        if self.size.width > self.size.height {
            newSize.width = CGFloat(maxDimension)
            newSize.height = CGFloat(maxDimension) * (self.size.height / self.size.width)
        } else {
            newSize.height = CGFloat(maxDimension)
            newSize.width = CGFloat(maxDimension) * (self.size.width / self.size.height)
        }
        UIGraphicsBeginImageContextWithOptions(
            /* size: */ newSize,
            /* opaque: */ false,
            /* scale: */ 1
        )
        defer { UIGraphicsEndImageContext() }

        self.draw(in: CGRect(origin: .zero, size: newSize))
        return UIGraphicsGetImageFromCurrentImageContext()
    }
}
public extension Image {
    func toHttpBody(maxDimension: Int = 2048) -> Single<HttpBody> {
        return self.load().flatMap { bmp in
            return Single.create { (em: SingleEmitter<HttpBody>) in
                let small = bmp.size.width * bmp.scale <= CGFloat(maxDimension) && bmp.size.height * bmp.scale <= CGFloat(maxDimension) ? bmp : bmp.resize(maxDimension: maxDimension)
                if let rep = small?.pngData() {
                    em.onSuccess(HttpBody(mediaType: "image/png", data: rep))
                } else {
                    em.onError(IllegalArgumentException("Could not turn image into a PNG."))
                }
            }
        }
    }
    func toHttpBodyRaw() -> Single<HttpBody> {
        switch(self){
        case let self as ImageRaw:
            return Single.just(HttpBody(mediaType: "image/*", data: self.raw))
        case let self as ImageBitmap:
            return Single.create { (em: SingleEmitter<HttpBody>) in
                if let rep = self.bitmap.pngData() {
                    em.onSuccess(HttpBody(mediaType: "image/png", data: rep))
                } else {
                    em.onError(IllegalArgumentException("Could not turn image into a PNG."))
                }
            }
        case let self as ImageResource:
            return self.load().flatMap { ImageBitmap(bitmap: $0).toHttpBodyRaw() }
        case let self as ImageReference:
            return self.uri.toHttpBody()
        case let self as ImageRemoteUrl:
            if let url = URL(string: self.url) {
                return url.toHttpBody()
            } else {
                return Single.error(IllegalArgumentException("Invalid URL \(self.url)"))
            }
        default:
            return Single.error(IllegalArgumentException("Unhandled image type \(self)"))
        }
    }
}

//--- multipartFormBody(HttpBodyPart)
public func multipartFormBody(_ parts: HttpBodyPart...) -> HttpBody {
    return multipartFormBody(parts: parts)
}

public func multipartFormBody(parts:Array<HttpBodyPart>) -> HttpBody {
    var body = Data()
    var stringBody = ""
    func emitText(_ string: String){
        stringBody += string
        body.append(string.data(using: .utf8)!)
    }
    let boundary = "\(arc4random())-\(arc4random())--"
    for part in parts {
        emitText("\r\n--" + boundary + "\r\n")
        switch part {
        case .file(let name, let filename, let subBody):
            emitText("Content-Disposition: form-data; name=\"\(name)\"; filename=\"\(filename ?? "file")\"\r\n")
            emitText("Content-Type: \(subBody.mediaType)\r\n\r\n")
            body.append(subBody.data)
            stringBody += "<binary data>"
        case .value(let name, let value):
            emitText("Content-Disposition: form-data; name=\"\(name)\"\r\n\r\n")
            emitText(value)
        }
    }
    emitText("\r\n--" + boundary + "\r\n")
    print("Made multipart: \(stringBody)")
    return HttpBody(mediaType: "multipart/form-data; boundary=\(boundary)", data: body)
}

public func multipartFormBody(_ parts: Array<HttpBodyPart>) -> HttpBody {
    return multipartFormBody(parts: parts)
}

public func multipartFormBody(parts: HttpBodyPart...) -> HttpBody {
    return multipartFormBody(parts: parts)
}

//--- multipartFormFilePart(String, String)
public func multipartFormFilePart(_ name: String, _ value: String) -> HttpBodyPart {
    return .value(name: name, value: value)
}
public func multipartFormFilePart(name: String, value: String) -> HttpBodyPart {
    return multipartFormFilePart(name, value)
}

//--- multipartFormFilePart(String, String? , HttpBody)
public func multipartFormFilePart(_ name: String, _ filename: String? , _ body: HttpBody) -> HttpBodyPart {
    return .file(name: name, filename: filename, body: body)
}
public func multipartFormFilePart(name: String, filename: String? , body: HttpBody) -> HttpBodyPart {
    return multipartFormFilePart(name, filename, body)
}

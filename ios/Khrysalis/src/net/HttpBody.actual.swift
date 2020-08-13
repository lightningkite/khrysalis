import Foundation

//--- HttpBody
public struct HttpBody {
    let mediaType: String
    let data: Data
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
public func kotlinAnyToJsonHttpBody(_ value: Encodable) -> HttpBody {
    return HttpBody(mediaType: HttpMediaTypes.JSON, data: kotlinAnyToJsonData(value))
}
public func kotlinAnyToJsonHttpBody(_ value: Dictionary<String, Any>) -> HttpBody {
    return HttpBody(mediaType: HttpMediaTypes.JSON, data: kotlinAnyToJsonData(value))
}
public func kotlinAnyToJsonHttpBody(_ value: Dictionary<String, Any?>) -> HttpBody {
    return HttpBody(mediaType: HttpMediaTypes.JSON, data: kotlinAnyToJsonData(value))
}
public func kotlinAnyToJsonHttpBody(_ value: Dictionary<String, Codable>) -> HttpBody {
    return HttpBody(mediaType: HttpMediaTypes.JSON, data: kotlinAnyToJsonData(value))
}
public func kotlinAnyToJsonHttpBody(_ value: Dictionary<String, Codable?>) -> HttpBody {
    return HttpBody(mediaType: HttpMediaTypes.JSON, data: kotlinAnyToJsonData(value))
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

//--- Bitmap.toHttpBody(Long)
public extension Bitmap {
    func toHttpBody(_ maxBytes: Int64) -> HttpBody {
        TODO()
    }
    func toHttpBody(maxBytes: Int64) -> HttpBody {
        return toHttpBody(maxBytes)
    }
}

//--- multipartFormBody(HttpBodyPart)
public func multipartFormBody(_ parts: HttpBodyPart...) -> HttpBody {
    return multipartFormBody(parts: parts)
}

public func multipartFormBody(parts:Array<HttpBodyPart>) -> HttpBody {
    var body = Data()
    let boundary = "-------\(arc4random())-\(arc4random())--"
    for part in parts {
        body.append((boundary + "\r\n").data(using: .utf8)!)
        switch part {
        case .file(let name, let filename, let subBody):
            body.append("Content-Disposition: form-data; name=\"\(name)\"; filename=\"\(filename)\"\r\n".data(using: .utf8)!)
            body.append("Content-Type: \(subBody.mediaType)\r\n".data(using: .utf8)!)
            body.append("\r\n".data(using: .utf8)!)
            body.append(subBody.data)
            body.append("\r\n".data(using: .utf8)!)
        case .value(let name, let value):
            body.append("Content-Disposition: form-data; name=\"\(name)\"\r\n".data(using: .utf8)!)
            body.append("\r\n".data(using: .utf8)!)
            body.append(value.data(using: .utf8)!)
            body.append("\r\n".data(using: .utf8)!)
        }
    }
    body.append((boundary + "\r\n").data(using: .utf8)!)
    return HttpBody(mediaType: "multipart/formdata; boundary=\(boundary)", data: body)
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

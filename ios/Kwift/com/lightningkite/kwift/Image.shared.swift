//Package: com.lightningkite.kwift
//Converted using Kwift2

import Foundation
import RxSwift
import RxRelay



public class Image {
    
    
    
    public init() {
    }
}
 

public class ImageReference: Image, Equatable, Hashable {
    
    public var uri: Uri
    
    public static func == (lhs: ImageReference, rhs: ImageReference) -> Bool {
        return lhs.uri == rhs.uri
    }
    public func hash(into hasher: inout Hasher) {
        hasher.combine(uri)
    }
    public func copy(
        uri: (Uri)? = nil
    ) -> ImageReference {
        return ImageReference(
            uri: uri ?? self.uri
        )
    }
    
    
    public init(uri: Uri) {
        self.uri = uri
        super.init()
    }
    convenience public init(_ uri: Uri) {
        self.init(uri: uri)
    }
}
 

public class ImageBitmap: Image, Equatable, Hashable {
    
    public var bitmap: Bitmap
    
    public static func == (lhs: ImageBitmap, rhs: ImageBitmap) -> Bool {
        return lhs.bitmap == rhs.bitmap
    }
    public func hash(into hasher: inout Hasher) {
        hasher.combine(bitmap)
    }
    public func copy(
        bitmap: (Bitmap)? = nil
    ) -> ImageBitmap {
        return ImageBitmap(
            bitmap: bitmap ?? self.bitmap
        )
    }
    
    
    public init(bitmap: Bitmap) {
        self.bitmap = bitmap
        super.init()
    }
    convenience public init(_ bitmap: Bitmap) {
        self.init(bitmap: bitmap)
    }
}
 

public class ImageRaw: Image, Equatable, Hashable {
    
    public var raw: Data
    
    public static func == (lhs: ImageRaw, rhs: ImageRaw) -> Bool {
        return lhs.raw == rhs.raw
    }
    public func hash(into hasher: inout Hasher) {
        hasher.combine(raw)
    }
    public func copy(
        raw: (Data)? = nil
    ) -> ImageRaw {
        return ImageRaw(
            raw: raw ?? self.raw
        )
    }
    
    
    public init(raw: Data) {
        self.raw = raw
        super.init()
    }
    convenience public init(_ raw: Data) {
        self.init(raw: raw)
    }
}
 

public class ImageRemoteUrl: Image, Equatable, Hashable {
    
    public var url: String
    
    public static func == (lhs: ImageRemoteUrl, rhs: ImageRemoteUrl) -> Bool {
        return lhs.url == rhs.url
    }
    public func hash(into hasher: inout Hasher) {
        hasher.combine(url)
    }
    public func copy(
        url: (String)? = nil
    ) -> ImageRemoteUrl {
        return ImageRemoteUrl(
            url: url ?? self.url
        )
    }
    
    
    public init(url: String) {
        self.url = url
        super.init()
    }
    convenience public init(_ url: String) {
        self.init(url: url)
    }
}
 
 

extension String {
    public func asImage() -> Image {
        return ImageRemoteUrl(self)
    }
}
 

extension Uri {
    public func asImage() -> Image {
        return ImageReference(self)
    }
}
 

extension Bitmap {
    public func asImage() -> Image {
        return ImageBitmap(self)
    }
}
 

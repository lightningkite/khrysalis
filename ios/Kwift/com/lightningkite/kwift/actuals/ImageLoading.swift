//
//  Image.swift
//  Alamofire
//
//  Created by Joseph Ivie on 12/3/19.
//

import Foundation


public func loadImage(_ image: Image, _ onResult: @escaping (Bitmap?)->Void){
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

public func loadImage(_ uri: Uri, _ onResult: @escaping (Bitmap?)->Void){
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

public func loadImage(_ uri: String, _ onResult: @escaping (Bitmap?)->Void){
    URLSession.shared.dataTask(with: Uri(string: uri)!, completionHandler: { data, response, error in
        DispatchQueue.main.async {
            if let data = data {
                onResult(UIImage(data: data))
            } else {
                onResult(nil)
            }
        }
    }).resume()
}

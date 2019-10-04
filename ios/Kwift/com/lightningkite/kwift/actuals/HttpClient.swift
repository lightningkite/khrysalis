//
//  HttpClient.swift
//  PennyProfit
//
//  Created by Joseph Ivie on 12/11/18.
//  Copyright © 2018 Shane Thompson. All rights reserved.
//

import Foundation
import Alamofire
import AlamofireImage

public enum HttpClient {
    
    public static let GET: String = "GET"
    public static let POST: String = "POST"
    public static let PUT: String = "PUT"
    public static let PATCH: String = "PATCH"
    public static let DELETE: String = "DELETE"
    
    public static func call<T: Codable>(
        url: String,
        method: String,
        headers: [String: String] = [:],
        body: Codable? = nil,
        onResult: @escaping (Int32, T?, String?) -> Void
    ) {
        print("HttpClient: Sending \(method) request to \(url) with headers \(headers)")
        let urlObj = URL(string: url)!
        
        let completionHandler = { (data:Data?, response:URLResponse?, error:Error?) in
            DispatchQueue.main.async {
                if let casted = response as? HTTPURLResponse, let data = data {
                    let dataString = String(data: data, encoding: .utf8) ?? ""
                    print("HttpClient: Response \(casted.statusCode): \(dataString)")
                    if casted.statusCode / 100 == 2 {
                        do {
                            let parsed = try T.fromJsonString(dataString)
                            print("HttpClient: Parsed: \(parsed)")
                            onResult(Int32(casted.statusCode), parsed, nil)
                        } catch {
                            print("HttpClient: Failed to parse due to: \(error.localizedDescription)")
                            onResult(Int32(casted.statusCode), nil, error.localizedDescription)
                        }
                    } else {
                        onResult(Int32(casted.statusCode), nil, dataString)
                    }
                } else {
                    print("HttpClient: ERROR!  Response is not URLResponse")
                    onResult(0, nil, "Failed")
                }
            }
        }
        
        var sessionConfig = URLSessionConfiguration.default
        sessionConfig.requestCachePolicy = .reloadIgnoringLocalCacheData
        sessionConfig.httpShouldSetCookies = false
        let session = URLSession(configuration: sessionConfig)
        var request = URLRequest(url: urlObj, cachePolicy: URLRequest.CachePolicy.reloadIgnoringCacheData, timeoutInterval: 15.0)
        request.setValue("application/json", forHTTPHeaderField: "Content-Type")
        request.setValue("application/json", forHTTPHeaderField: "Accept")
        for (key, value) in headers {
            request.setValue(value, forHTTPHeaderField: key)
        }
        request.httpMethod = method
        
        if let body = body {
            let bodyData = try? body.toJsonData()
            session.uploadTask(with: request, from: bodyData!, completionHandler: completionHandler).resume()
            let asString = String(data: bodyData!, encoding: .utf8)
            print("HttpClient: with body \(asString ?? "")")
        } else {
            session.dataTask(with: request, completionHandler: completionHandler).resume()
        }
    }
    
    public static func callWithoutResult(
        url: String,
        method: String,
        headers: [String: String] = [:],
        body: Codable? = nil,
        onResult: @escaping (Int32, String?) -> Void
    ) {
        print("HttpClient: Sending \(method) request to \(url) with headers \(headers)")
        let urlObj = URL(string: url)!
        
        let completionHandler = { (data:Data?, response:URLResponse?, error:Error?) in
            DispatchQueue.main.async {
                if let casted = response as? HTTPURLResponse, let data = data {
                    let dataString = String(data: data, encoding: .utf8) ?? ""
                    print("HttpClient: Response \(casted.statusCode): \(dataString)")
                    if casted.statusCode / 100 == 2 {
                        onResult(Int32(casted.statusCode), nil)
                    } else {
                        onResult(Int32(casted.statusCode), dataString)
                    }
                } else {
                    print("HttpClient: ERROR!  Response is not URLResponse")
                    onResult(0, "Failed")
                }
            }
        }
        
        let sessionConfig = URLSessionConfiguration.default
        let session = URLSession(configuration: sessionConfig)
        var request = URLRequest(url: urlObj, cachePolicy: URLRequest.CachePolicy.reloadIgnoringCacheData, timeoutInterval: 15.0)
        request.setValue("application/json", forHTTPHeaderField: "Content-Type")
        request.setValue("application/json", forHTTPHeaderField: "Accept")
        for (key, value) in headers {
            request.setValue(value, forHTTPHeaderField: key)
        }
        request.httpMethod = method
        
        if let body = body {
            let bodyData = try? body.toJsonData()
            session.uploadTask(with: request, from: bodyData!, completionHandler: completionHandler).resume()
            let asString = String(data: bodyData!, encoding: .utf8)
            print("HttpClient: with body \(asString ?? "")")
        } else {
            session.dataTask(with: request, completionHandler: completionHandler).resume()
        }
    }
    
    public static func uploadImage<T: Codable>(
        url: String,
        method: String,
        headers: [String: String],
        fieldName: String,
        image: ImageData,
        onResult: @escaping (Int32, T?, String?) -> Void
    ) {
        let imgData = image.jpegData(compressionQuality: 0.1)!
        
        var httpMethod: HTTPMethod = .post
        if(method.caseInsensitiveCompare("post") == .orderedSame){
            httpMethod = .post
        }
        if(method.caseInsensitiveCompare("put") == .orderedSame){
            httpMethod = .put
        }
        if(method.caseInsensitiveCompare("patch") == .orderedSame){
            httpMethod = .patch
        }
        
        Alamofire.upload(multipartFormData: { multipartFormData in
            multipartFormData.append(imgData, withName: fieldName, fileName: "file.jpg", mimeType: "image/jpg")
        }, to:url, method: httpMethod, headers: headers)
        { (result) in
            switch result {
            case .success(let upload, _, _):
                
                upload.uploadProgress(closure: { (progress) in
                    print("Upload Progress: \(progress.fractionCompleted)")
                })
                
                upload.responseJSON { response in
                    DispatchQueue.main.async {
                        if let casted = response.response, let data = response.data {
                            let dataString = String(data: data, encoding: .utf8) ?? ""
                            print("HttpClient: Response \(casted.statusCode): \(dataString)")
                            if casted.statusCode / 100 == 2 {
                                do {
                                    let parsed = try T.fromJsonString(dataString)
                                    onResult(Int32(casted.statusCode), parsed, nil)
                                } catch {
                                    print("HttpClient: Failed to parse due to: \(error.localizedDescription)")
                                    onResult(Int32(casted.statusCode), nil, error.localizedDescription)
                                }
                            } else {
                                onResult(Int32(casted.statusCode), nil, dataString)
                            }
                        } else {
                            onResult(0, nil, "Failed")
                        }
                    }
                }
                
            case .failure(let encodingError):
                DispatchQueue.main.async {
                    print(encodingError)
                    onResult(400, nil, encodingError.localizedDescription)
                }
            }
        }
    }
    
    public static func uploadImageWithoutResult(
        url: String,
        method: String,
        headers: [String: String],
        fieldName: String,
        image: ImageData,
        onResult: @escaping (Int32, String?) -> Void
    ) {
        let imgData = image.jpegData(compressionQuality: 0.1)!
        
        var httpMethod: HTTPMethod = .post
        if(method.caseInsensitiveCompare("post") == .orderedSame){
            httpMethod = .post
        }
        if(method.caseInsensitiveCompare("put") == .orderedSame){
            httpMethod = .put
        }
        if(method.caseInsensitiveCompare("patch") == .orderedSame){
            httpMethod = .patch
        }
        
        Alamofire.upload(multipartFormData: { multipartFormData in
            multipartFormData.append(imgData, withName: fieldName, fileName: "file.jpg", mimeType: "image/jpg")
        }, to:url, method: httpMethod, headers: headers)
        { (result) in
            switch result {
            case .success(let upload, _, _):
                
                upload.uploadProgress(closure: { (progress) in
                    print("Upload Progress: \(progress.fractionCompleted)")
                })
                
                upload.responseJSON { response in
                    DispatchQueue.main.async {
                        if let casted = response.response, let data = response.data {
                            let dataString = String(data: data, encoding: .utf8) ?? ""
                            print("HttpClient: Response \(casted.statusCode): \(dataString)")
                            if casted.statusCode / 100 == 2 {
                                onResult(Int32(casted.statusCode), nil)
                            } else {
                                onResult(Int32(casted.statusCode), dataString)
                            }
                        } else {
                            onResult(0, "Failed")
                        }
                    }
                }
                
            case .failure(let encodingError):
                DispatchQueue.main.async {
                    print(encodingError)
                    onResult(400, encodingError.localizedDescription)
                }
            }
        }
    }
}

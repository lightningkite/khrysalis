//Stub file made with Khrysalis 2 (by Lightning Kite)
import Foundation
import AlamofireImage
import Alamofire


//--- HttpClient.{
public enum HttpClient {
    
    //--- HttpClient.immediateMode
    public static var immediateMode: Bool = false
    
    //--- HttpClient.GET
    public static let GET: String = "GET"
    
    //--- HttpClient.POST
    public static let POST: String = "POST"
    
    //--- HttpClient.PUT
    public static let PUT: String = "PUT"
    
    //--- HttpClient.PATCH
    public static let PATCH: String = "PATCH"
    
    //--- HttpClient.DELETE
    public static let DELETE: String = "DELETE"
    
    //--- HttpClient.call(String, String, Map<String,String>, Any? ,  @escaping()(code:Int,result:T?,error:String?)->Unit)
    public static func call<T: Decodable>(_ url: String, _ method: String = GET, _ headers: Dictionary<String, String> = [:], _ body: Encodable? = nil, _ onResult: @escaping (_ code: Int32, _ result: T?, _ error: String?) -> Void) -> Void {
        print("HttpClient: Sending \(method) request to \(url) with headers \(headers)")
        let urlObj = URL(string: url)!
        
        let completionHandler = { (data:Data?, response:URLResponse?, error:Error?) in
            DispatchQueue.main.async {
                if let casted = response as? HTTPURLResponse, let data = data {
                    let dataString = String(data: data, encoding: .utf8) ?? ""
                    print("HttpClient: Response \(casted.statusCode): \(dataString)")
                    if casted.statusCode / 100 == 2 {
                        if T.self == String.self {
                            onResult(Int32(casted.statusCode), dataString as? T, nil)
                        } else {
                            do {
                                let parsed = try T.fromJsonString(dataString)
                                print("HttpClient: Parsed: \(parsed)")
                                onResult(Int32(casted.statusCode), parsed, nil)
                            } catch {
                                print("HttpClient: Failed to parse due to: \(error.localizedDescription)")
                                onResult(Int32(casted.statusCode), nil, error.localizedDescription)
                            }
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
        
        let sessionConfig = URLSessionConfiguration.default
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
    public static func call<T: Decodable>(url: String, method: String = GET, headers: Dictionary<String, String> = [:], body: Encodable? = nil, onResult: @escaping (_ code: Int32, _ result: T?, _ error: String?) -> Void) -> Void {
        return call(url, method, headers, body, onResult)
    }
    
    //--- HttpClient.callWithoutResult(String, String, Map<String,String>, Any? ,  @escaping()(code:Int,error:String?)->Unit)
    public static func callWithoutResult(_ url: String, _ method: String = GET, _ headers: Dictionary<String, String> = [:], _ body: Encodable? = nil, _ onResult: @escaping (_ code: Int32, _ error: String?) -> Void) -> Void {
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
    public static func callWithoutResult(url: String, method: String = GET, headers: Dictionary<String, String> = [:], body: Encodable? = nil, onResult: @escaping (_ code: Int32, _ error: String?) -> Void) -> Void {
        return callWithoutResult(url, method, headers, body, onResult)
    }
    
    //--- HttpClient.uploadImageWithoutResult(String, String, Map<String,String>, String, Image, Long, Map<String,String>,  @escaping()(code:Int,error:String?)->Unit)
    public static func uploadImageWithoutResult(_ url: String, _ method: String = POST, _ headers: Dictionary<String, String> = [:], _ fieldName: String, _ image: Image, _ maxSize: Int64 = 10_000_000, _ additionalFields: Dictionary<String, String> = [:], _ onResult: @escaping (_ code: Int32, _ error: String?) -> Void) -> Void {
        loadImage(image) { image in
            if let image = image {
                var quality: CGFloat = 1.0
                var imgData = image.jpegData(compressionQuality: quality)!
                while imgData.count > maxSize {
                    quality -= 0.1
                    imgData = image.jpegData(compressionQuality: quality)!
                }
                
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
                    for (key, value) in additionalFields {
                        multipartFormData.append(value.data(using: .utf8)!, withName: key)
                    }
                    multipartFormData.append(imgData, withName: fieldName, fileName: "file.jpg", mimeType: "image/jpeg")
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
    }
    public static func uploadImageWithoutResult(url: String, method: String = POST, headers: Dictionary<String, String> = [:], fieldName: String, image: Image, maxSize: Int64 = 10_000_000, additionalFields: Dictionary<String, String> = [:], onResult: @escaping (_ code: Int32, _ error: String?) -> Void) -> Void {
        return uploadImageWithoutResult(url, method, headers, fieldName, image, maxSize, additionalFields, onResult)
    }
    
    //--- HttpClient.uploadImage(String, String, Map<String,String>, String, Image, Long, Map<String,String>,  @escaping()(code:Int,result:T?,error:String?)->Unit)
    public static func uploadImage<T: Decodable>(_ url: String, _ method: String = POST, _ headers: Dictionary<String, String> = [:], _ fieldName: String, _ image: Image, _ maxSize: Int64 = 10_000_000, _ additionalFields: Dictionary<String, String> = [:], _ onResult: @escaping (_ code: Int32, _ result: T?, _ error: String?) -> Void) -> Void {
        loadImage(image) { image in
            if let image = image {
                var quality: CGFloat = 1.0
                var imgData = image.jpegData(compressionQuality: quality)!
                while imgData.count > maxSize {
                    quality -= 0.1
                    imgData = image.jpegData(compressionQuality: quality)!
                }
                
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
                    multipartFormData.append(imgData, withName: fieldName, fileName: "file.jpg", mimeType: "image/jpeg")
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
                                        if T.self == String.self {
                                            onResult(Int32(casted.statusCode), dataString as? T, nil)
                                        } else {
                                            do {
                                                let parsed = try T.fromJsonString(dataString)
                                                onResult(Int32(casted.statusCode), parsed, nil)
                                            } catch {
                                                print("HttpClient: Failed to parse due to: \(error.localizedDescription)")
                                                onResult(Int32(casted.statusCode), nil, error.localizedDescription)
                                            }
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
        }
    }
    public static func uploadImage<T: Decodable>(url: String, method: String = POST, headers: Dictionary<String, String> = [:], fieldName: String, image: Image, maxSize: Int64 = 10_000_000, additionalFields: Dictionary<String, String> = [:], onResult: @escaping (_ code: Int32, _ result: T?, _ error: String?) -> Void) -> Void {
        return uploadImage(url, method, headers, fieldName, image, maxSize, additionalFields, onResult)
    }
    
    //--- HttpClient.}
}

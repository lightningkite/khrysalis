//Stub file made with Khrysalis 2 (by Lightning Kite)
import Foundation
import AlamofireImage
import Alamofire
import RxSwift
import Starscream


//--- HttpClient.{
public enum HttpClient {
    
    static public let INSTANCE = Self.self

//--- HttpClient.ioScheduler
    public static var ioScheduler: Scheduler? = Schedulers.io()
    
    //--- HttpClient.responseScheduler
    public static var responseScheduler: Scheduler? = MainScheduler.instance
    //--- HttpClient.Single<T>.threadCorrectly()
//--- HttpClient.Observable<T>.threadCorrectly()
    
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
    
    
    //--- CleanUrl
    //--- Encodes the url as needed such as replace a space with %20 in a query param.
    //--- URL(string) does not encode a url, and will crash if a space exists in the query params or some other 'invalid' character
    //--- Probably should be expanded to encode more parts of the url
    public static func cleanURL(_ url:String)->String{
        if url.contains("?"){
            let front = url.substringBefore(delimiter: "?")
            let back = url.substringAfter(delimiter: "?")
            let backParts = back.split(separator: "&")
            let fixedBack = backParts.joinToString("&") {
                $0.addingPercentEncoding(withAllowedCharacters: CharacterSet.urlQueryAllowed.union( CharacterSet(charactersIn: "%"))) ?? String($0)
            }
            return "\(front)?\(fixedBack)"
        }else{
            return url
        }
    }

    //--- HttpClient.call(String, String, Map<String,String>, HttpBody? )
    public static var defaultOptions = HttpOptions()
    public static func call(url: String, method: String = "GET", headers: Dictionary<String, String> = [:], body: HttpBody? = nil, options: HttpOptions = HttpClient.defaultOptions) -> Single<HttpResponse> {
        print("HttpClient: Sending \(method) request to \(url) with headers \(headers)")
        print(cleanURL(url))
        let urlObj = URL(string: cleanURL(url))!
        var single = Single.create { (emitter: SingleEmitter<HttpResponse>) in
            let completionHandler = { (data:Data?, response:URLResponse?, error:Error?) in
                if let casted = response as? HTTPURLResponse, let data = data {
                    print("HttpClient: Response from \(method) request to \(urlObj) with headers \(headers): \(casted.statusCode)")
                    emitter.onSuccess(HttpResponse(response: casted, data: data))
                } else {
                    print("HttpClient: ERROR!  Response is not URLResponse")
                    emitter.onError(IllegalStateException("Response is not URLResponse"))
                }
            }
            
            var cachePolicy = URLRequest.CachePolicy.reloadIgnoringCacheData
            switch(options.cacheMode){
            case .Default:
                cachePolicy = .reloadRevalidatingCacheData
            case .NoStore:
                cachePolicy = .reloadRevalidatingCacheData
            case .Reload:
                cachePolicy = .reloadIgnoringLocalAndRemoteCacheData
            case .NoCache:
                cachePolicy = .reloadRevalidatingCacheData
            case .ForceCache:
                cachePolicy = .returnCacheDataElseLoad
            case .OnlyIfCached:
                cachePolicy = .returnCacheDataDontLoad
            }
            
            var totalTimeout = options.callTimeout ?? 0
            if let c = options.connectTimeout, let w = options.writeTimeout, let r = options.readTimeout {
                if c == 0 || w == 0 || r == 0 {
                    totalTimeout = 0
                } else {
                    totalTimeout = c + w + r
                }
            }
            let totalTimeoutInterval = totalTimeout == 0 ? 60.0 * 15.0 : TimeInterval(totalTimeout / 1000)
            
            let sessionConfig = URLSessionConfiguration.default
            sessionConfig.requestCachePolicy = cachePolicy
            sessionConfig.timeoutIntervalForResource = totalTimeoutInterval
            sessionConfig.timeoutIntervalForRequest = totalTimeoutInterval
            sessionConfig.httpShouldSetCookies = false
            let session = URLSession(configuration: sessionConfig)
            var request = URLRequest(url: urlObj, cachePolicy: URLRequest.CachePolicy.reloadIgnoringCacheData, timeoutInterval: totalTimeoutInterval)
            
            if headers["Accept"] == nil {
                request.setValue("application/json", forHTTPHeaderField: "Accept")
            }
            for (key, value) in headers {
                request.setValue(value, forHTTPHeaderField: key)
            }
            request.httpMethod = method

            if let body = body {
                request.setValue(body.mediaType, forHTTPHeaderField: "Content-Type")
                session.uploadTask(with: request, from: body.data, completionHandler: completionHandler).resume()
            } else {
                session.dataTask(with: request, completionHandler: completionHandler).resume()
            }
        }
        if let io = ioScheduler {
            single = single.subscribeOn(io)
        }
        if let resp = responseScheduler {
            single = single.observeOn(resp)
        }
        return single
    }
    
//    private class CallDelegate: NSObject, URLSessionDelegate, URLSessionDataDelegate {
//        var len: Int64 = -1
//        var downloaded: Int = 0
//        func urlSession(_ session: URLSession, dataTask: URLSessionDataTask, didReceive response: URLResponse, completionHandler: @escaping (URLSession.ResponseDisposition) -> Void) {
//            len = response.expectedContentLength
//            completionHandler(.allow)
//        }
//        func urlSession(_ session: URLSession, dataTask: URLSessionDataTask, didReceive data: Data) {
//            downloaded += data.count
//        }
//    }
    public static func callWithProgress(url: String, method: String = "GET", headers: Dictionary<String, String> = [:], body: HttpBody? = nil, options: HttpOptions = HttpClient.defaultOptions) -> Pair<ObservableProperty<HttpProgress>, Single<HttpResponse>> {
        print("HttpClient: Sending \(method) request to \(url) with headers \(headers)")
        let toHold: Box<Array<Any>> = Box([])
        let urlObj = URL(string: cleanURL(url))!
        let progSubj = PublishSubject<HttpProgress>()
        var progObs: Observable<HttpProgress> = progSubj
        var single = Single.create { (emitter: SingleEmitter<HttpResponse>) in
            let completionHandler = { [toHold] (data:Data?, response:URLResponse?, error:Error?) in
                progSubj.onNext(HttpProgress.Companion.INSTANCE.done)
                if let casted = response as? HTTPURLResponse, let data = data {
                    print("HttpClient: Response from \(method) request to \(url) with headers \(headers): \(casted.statusCode)")
                    emitter.onSuccess(HttpResponse(response: casted, data: data))
                } else {
                    print("HttpClient: ERROR!  Response is not URLResponse")
                    emitter.onError(IllegalStateException("Response is not URLResponse"))
                }
            }
            
            var cachePolicy = URLRequest.CachePolicy.reloadIgnoringCacheData
            switch(options.cacheMode){
            case .Default:
                cachePolicy = .reloadRevalidatingCacheData
            case .NoStore:
                cachePolicy = .reloadRevalidatingCacheData
            case .Reload:
                cachePolicy = .reloadIgnoringLocalAndRemoteCacheData
            case .NoCache:
                cachePolicy = .reloadRevalidatingCacheData
            case .ForceCache:
                cachePolicy = .returnCacheDataElseLoad
            case .OnlyIfCached:
                cachePolicy = .returnCacheDataDontLoad
            }
            
            var totalTimeout = options.callTimeout ?? 0
            if let c = options.connectTimeout, let w = options.writeTimeout, let r = options.readTimeout {
                if c == 0 || w == 0 || r == 0 {
                    totalTimeout = 0
                } else {
                    totalTimeout = c + w + r
                }
            }
            let totalTimeoutInterval = totalTimeout == 0 ? 60.0 * 15.0 : TimeInterval(totalTimeout / 1000)
            
            let sessionConfig = URLSessionConfiguration.default
            sessionConfig.requestCachePolicy = cachePolicy
            sessionConfig.timeoutIntervalForResource = totalTimeoutInterval
            sessionConfig.timeoutIntervalForRequest = totalTimeoutInterval
            sessionConfig.httpShouldSetCookies = false
            let session = URLSession(configuration: sessionConfig)
            var request = URLRequest(url: urlObj, cachePolicy: URLRequest.CachePolicy.reloadIgnoringCacheData, timeoutInterval: totalTimeoutInterval)
            if headers["Accept"] == nil {
                request.setValue("application/json", forHTTPHeaderField: "Accept")
            }
            for (key, value) in headers {
                request.setValue(value, forHTTPHeaderField: key)
            }
            request.httpMethod = method

            if let body = body {
                request.setValue(body.mediaType, forHTTPHeaderField: "Content-Type")
                let task = session.uploadTask(with: request, from: body.data, completionHandler: completionHandler)
                let obs = task.progress.observe(\.fractionCompleted) { (progress, _) in
                    progSubj.onNext(HttpProgress(phase: .Read, ratio: Float(progress.fractionCompleted)))
                }
                toHold.value.append(obs)
                task.resume()
            } else {
                let task = session.dataTask(with: request, completionHandler: completionHandler)
                let obs = task.progress.observe(\.fractionCompleted) { (progress, _) in
                    progSubj.onNext(HttpProgress(phase: .Read, ratio: Float(progress.fractionCompleted)))
                }
                toHold.value.append(obs)
                task.resume()
            }
        }
        if let io = ioScheduler {
            single = single.subscribeOn(io)
            progObs = progObs.subscribeOn(io)
        }
        if let resp = responseScheduler {
            single = single.observeOn(resp)
            progObs = progObs.observeOn(resp)
        }
        return Pair(first: progObs.asObservableProperty(defaultValue: HttpProgress.Companion.INSTANCE.connecting), second: single)
    }
    
    //--- HttpClient.call(String, String, Map<String,String>, HttpBody? )
    public static func call(url: String, method: String = "GET", headers: Dictionary<String, String> = [:], body: HttpBody? = nil, callTimeout:Int64? = nil, writeTimeout:Int64? = nil, readTimeout:Int64?=nil,connectTimeout:Int64?=nil) -> Single<HttpResponse> {
        return call(url: url, method: method, headers: headers, body: body, options: HttpOptions(
            callTimeout: callTimeout,
            writeTimeout: writeTimeout,
            readTimeout: readTimeout,
            connectTimeout: connectTimeout
        ))
    }
      
    
    
    
    //--- HttpClient.call(String, String, Map<String,String>, Any? ,  @escaping()(code:Int,result:T?,error:String?)->Unit)
    public static func call<T: Decodable>(_ url: String, _ method: String = GET, _ headers: Dictionary<String, String> = [:], _ body: Encodable? = nil, _ onResult: @escaping (_ code: Int, _ result: T?, _ error: String?) -> Void) -> Void {
        print("HttpClient: Sending \(method) request to \(url) with headers \(headers)")
        let urlObj = URL(string: cleanURL(url))!

        let completionHandler = { (data:Data?, response:URLResponse?, error:Error?) in
            DispatchQueue.main.async {
                if let casted = response as? HTTPURLResponse, let data = data {
                    let dataString = String(data: data, encoding: .utf8) ?? ""
                    print("HttpClient: Response \(casted.statusCode): \(dataString)")
                    if casted.statusCode / 100 == 2 {
                        do {
                            let parsed = try T.fromJsonString(dataString)
                            print("HttpClient: Parsed: \(parsed)")
                            onResult(Int(casted.statusCode), parsed, nil)
                        } catch {
                            print("HttpClient: Failed to parse due to: \(error.localizedDescription)")
                            onResult(Int(casted.statusCode), nil, error.localizedDescription)
                        }
                    } else {
                        onResult(Int(casted.statusCode), nil, dataString)
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
    public static func call<T: Decodable>(url: String, method: String = GET, headers: Dictionary<String, String> = [:], body: Encodable? = nil, onResult: @escaping (_ code: Int, _ result: T?, _ error: String?) -> Void) -> Void {
        return call(url, method, headers, body, onResult)
    }

    //--- HttpClient.callRaw(String, String, Map<String,String>, Any? ,  @escaping()(code:Int,result:String?,error:String?)->Unit)
    public static func callRaw(_ url: String, _ method: String = GET, _ headers: Dictionary<String, String> = [:], _ body: Encodable? = nil, _ onResult: @escaping (_ code: Int, _ result: String?, _ error: String?) -> Void) -> Void {
        print("HttpClient: Sending \(method) request to \(url) with headers \(headers)")
        let urlObj = URL(string: cleanURL(url))!

        let completionHandler = { (data:Data?, response:URLResponse?, error:Error?) in
            DispatchQueue.main.async {
                if let casted = response as? HTTPURLResponse, let data = data {
                    let dataString = String(data: data, encoding: .utf8) ?? ""
                    print("HttpClient: Response \(casted.statusCode): \(dataString)")
                    if casted.statusCode / 100 == 2 {
                        onResult(Int(casted.statusCode), dataString, nil)
                    } else {
                        onResult(Int(casted.statusCode), nil, dataString)
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
    public static func callRaw(url: String, method: String = GET, headers: Dictionary<String, String> = [:], body: Encodable? = nil, onResult: @escaping (_ code: Int, _ result: String?, _ error: String?) -> Void) -> Void {
        return callRaw(url, method, headers, body, onResult)
    }

    //--- HttpClient.callWithoutResult(String, String, Map<String,String>, Any? ,  @escaping()(code:Int,error:String?)->Unit)
    public static func callWithoutResult(_ url: String, _ method: String = GET, _ headers: Dictionary<String, String> = [:], _ body: Encodable? = nil, _ onResult: @escaping (_ code: Int, _ error: String?) -> Void) -> Void {
        print("HttpClient: Sending \(method) request to \(url) with headers \(headers)")
        let urlObj = URL(string: cleanURL(url))!

        let completionHandler = { (data:Data?, response:URLResponse?, error:Error?) in
            DispatchQueue.main.async {
                if let casted = response as? HTTPURLResponse, let data = data {
                    let dataString = String(data: data, encoding: .utf8) ?? ""
                    print("HttpClient: Response \(casted.statusCode): \(dataString)")
                    if casted.statusCode / 100 == 2 {
                        onResult(Int(casted.statusCode), nil)
                    } else {
                        onResult(Int(casted.statusCode), dataString)
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
    public static func callWithoutResult(url: String, method: String = GET, headers: Dictionary<String, String> = [:], body: Encodable? = nil, onResult: @escaping (_ code: Int, _ error: String?) -> Void) -> Void {
        return callWithoutResult(url, method, headers, body, onResult)
    }

    //--- HttpClient.uploadImageWithoutResult(String, String, Map<String,String>, String, Image, Long, Map<String,String>,  @escaping()(code:Int,error:String?)->Unit)
    public static func uploadImageWithoutResult(_ url: String, _ method: String = POST, _ headers: Dictionary<String, String> = [:], _ fieldName: String, _ image: Image, _ maxSize: Int64 = 10_000_000, _ additionalFields: Dictionary<String, String> = [:], _ onResult: @escaping (_ code: Int, _ error: String?) -> Void) -> Void {
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
                                        onResult(Int(casted.statusCode), nil)
                                    } else {
                                        onResult(Int(casted.statusCode), dataString)
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
    public static func uploadImageWithoutResult(url: String, method: String = POST, headers: Dictionary<String, String> = [:], fieldName: String, image: Image, maxSize: Int64 = 10_000_000, additionalFields: Dictionary<String, String> = [:], onResult: @escaping (_ code: Int, _ error: String?) -> Void) -> Void {
        return uploadImageWithoutResult(url, method, headers, fieldName, image, maxSize, additionalFields, onResult)
    }

    //--- HttpClient.uploadImage(String, String, Map<String,String>, String, Image, Long, Map<String,String>,  @escaping()(code:Int,result:T?,error:String?)->Unit)
    public static func uploadImage<T: Decodable>(_ url: String, _ method: String = POST, _ headers: Dictionary<String, String> = [:], _ fieldName: String, _ image: Image, _ maxSize: Int64 = 10_000_000, _ additionalFields: Dictionary<String, String> = [:], _ onResult: @escaping (_ code: Int, _ result: T?, _ error: String?) -> Void) -> Void {
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
                                        do {
                                            let parsed = try T.fromJsonString(dataString)
                                            onResult(Int(casted.statusCode), parsed, nil)
                                        } catch {
                                            print("HttpClient: Failed to parse due to: \(error.localizedDescription)")
                                            onResult(Int(casted.statusCode), nil, error.localizedDescription)
                                        }
                                    } else {
                                        onResult(Int(casted.statusCode), nil, dataString)
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
    public static func uploadImage<T: Decodable>(url: String, method: String = POST, headers: Dictionary<String, String> = [:], fieldName: String, image: Image, maxSize: Int64 = 10_000_000, additionalFields: Dictionary<String, String> = [:], onResult: @escaping (_ code: Int, _ result: T?, _ error: String?) -> Void) -> Void {
        return uploadImage(url, method, headers, fieldName, image, maxSize, additionalFields, onResult)
    }

    //--- HttpClient.webSocket(String)
    static public func webSocket(_ url: String) -> Observable<ConnectedWebSocket> {
        return Observable.using({ () -> ConnectedWebSocket in
            var out = ConnectedWebSocket(url: url)
            var request = URLRequest(url: URL(string: cleanURL(url))!)
            let socket = WebSocket(request: request)
            socket.delegate = out
            out.underlyingSocket = socket
            socket.connect()
            return out
        }, observableFactory: { $0.ownConnection })
    }
    static public func webSocket(url: String) -> Observable<ConnectedWebSocket> {
        return webSocket(url)
    }

    //--- HttpClient.}
}

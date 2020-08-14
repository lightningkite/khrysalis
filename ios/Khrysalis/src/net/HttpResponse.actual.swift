
import Foundation
import RxSwift

//--- HttpResponse
public struct HttpResponse {
    public let response: HTTPURLResponse
    public let data: Data
    public init(response: HTTPURLResponse, data: Data) {
        self.response = response
        self.data = data
    }

    //--- HttpResponse.code
    public var code: Int {
        return Int(self.response.statusCode)
    }

    //--- HttpResponse.isSuccessful
    public var isSuccessful: Bool {
        let code = self.response.statusCode
        return code >= 200 && code < 300
    }


    //--- HttpResponse.headers
    public var headers: Dictionary<String, String> {
        return Dictionary(self.response.allHeaderFields
        .filter { it in it.0 is String && it.1 is String }
        .map { it in (it.key as! String, it.value as! String) }, uniquingKeysWith: { _, a in a} )
    }


    //--- HttpResponse.discard()
    public func discard() -> Single<Void> {
        //Do nothing - in iOS, the data is already read
        return Single.just(())
    }

    //--- HttpResponse.readText()
    public func readText() -> Single<String> {
        return Single.just(String(data: data, encoding: .utf8)!)
    }


    //--- HttpResponse.readData()
    public func readData() -> Single<Data> {
        return Single.just(data)
    }
    

    //--- HttpResponse.readJson()
    public func readJson<T: Codable>() -> Single<T> {
        do {
            return Single.just(try T.fromJsonData(data))
        } catch {
            return Single.error(error)
        }
    }

    //--- HttpResponse.readJsonDebug()
    public func readJsonDebug<T: Codable>() -> Single<T> {
        do {
            print("Got response \(String(data: data, encoding: .utf8)!)")
            return Single.just(try T.fromJsonData(data))
        } catch {
            return Single.error(error)
        }
    }
    

}

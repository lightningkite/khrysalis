//Package: com.lightningkite.khrysalis.net
//Converted using Khrysalis2

import Foundation
import RxSwift
import RxRelay



//--- Single<@swiftExactly("Element")HttpResponse>.unsuccessfulAsError()
extension Single where Element == HttpResponse, Trait == SingleTrait {
    public func unsuccessfulAsError() -> Single<HttpResponse> {
        return self.map{ (it) in
            if it.isSuccessful {
                return it
            } else {
                throw HttpResponseException(it)
            }
        }
    }
}
 
 
 

extension PrimitiveSequence where Element == HttpResponse, Trait == SingleTrait {
    //--- Single<@swiftExactly("Element")HttpResponse>.readJson()
    public func readJson<T: Codable>() -> Single<T> {
        return self.map{ (it) in
            if it.isSuccessful {
                return it.readJson()
            } else {
                throw HttpResponseException(it)
            }
        }
    }
    public func readJson<T: Codable>(_ type: T.Type) -> Single<T> {
        return self.map{ (it) in
            if it.isSuccessful {
                return it.readJson()
            } else {
                throw HttpResponseException(it)
            }
        }
    }
    //--- Single<@swiftExactly("Element")HttpResponse>.readText()
    public func readText() -> Single<String> {
        return self.map{ (it) in
            if it.isSuccessful {
                return String(data: it.data, encoding: .utf8)!
            } else {
                throw HttpResponseException(it)
            }
        }
    }
    //--- Single<@swiftExactly("Element")HttpResponse>.readData()
    public func readData() -> Single<Data> {
        return self.map{ (it) in
            if it.isSuccessful {
                return it.data
            } else {
                throw HttpResponseException(it)
            }
        }
    }
    //--- Single<Element>.readHttpException()
    func readHttpException<Element>() -> Single<Element> {
        return self.catchError { error -> Single<Element> in
            if error is HttpResponseException {
                throw HttpReadResponseException(error.response, error.readText(), error.cause)
            }
        }
    }
}

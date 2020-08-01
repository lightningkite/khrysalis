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
        return self.flatMap { (it) in
            if it.isSuccessful {
                return it.readJson()
            } else {
                Single.error(HttpResponseException(it))
            }
        }
    }
    //--- Single<@swiftExactly("Element")HttpResponse>.readJsonDebug()
    public func readJsonDebug<T: Codable>() -> Single<T> {
        return self.flatMap { (it) in
            if it.isSuccessful {
                return it.readJsonDebug()
            } else {
                Single.error(HttpResponseException(it))
            }
        }
    }
    public func readJson<T: Codable>(_ type: T.Type) -> Single<T> {
        return self.flatMap { (it) in
            if it.isSuccessful {
                return it.readJson()
            } else {
                Single.error(HttpResponseException(it))
            }
        }
    }
    //--- Single<@swiftExactly("Element")HttpResponse>.readText()
    public func readText() -> Single<String> {
        return self.flatMap { (it) in
            if it.isSuccessful {
                return String(data: it.data, encoding: .utf8)!
            } else {
                Single.error(HttpResponseException(it))
            }
        }
    }
    //--- Single<@swiftExactly("Element")HttpResponse>.readData()
    public func readData() -> Single<Data> {
        return self.flatMap { (it) in
            if it.isSuccessful {
                return it.data
            } else {
                Single.error(HttpResponseException(it))
            }
        }
    }
}

extension PrimitiveSequence where Trait == SingleTrait {
    //--- Single<Element>.readHttpException()
    func readHttpException() -> PrimitiveSequence<SingleTrait, Element> {
        return self.catchError { (error) -> PrimitiveSequence<SingleTrait, Element> in
            switch error {
            case let HttpResponseExceptions.Exception(response, cause):
                return Single<Element>.error(HttpReadResponseException(response, response.readText(), cause))
            default:
                return Single<Element>.error(error)
            }
        }
    }
}

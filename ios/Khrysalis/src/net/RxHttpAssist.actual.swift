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
    //--- Single<@swiftExactly("Element")HttpResponse>.discard()
    public func discard() -> Single<Void> {
        return self.flatMap { (it) in
            if it.isSuccessful {
                return it.discard()
            } else {
                return Single.error(HttpResponseException(it))
            }
        }
    }
    //--- Single<@swiftExactly("Element")HttpResponse>.readJson()
    public func readJson<T: Codable>() -> Single<T> {
        return self.flatMap { (it) in
            if it.isSuccessful {
                return it.readJson()
            } else {
                return Single.error(HttpResponseException(it))
            }
        }
    }
    //--- Single<@swiftExactly("Element")HttpResponse>.readJsonDebug()
    public func readJsonDebug<T: Codable>() -> Single<T> {
        return self.flatMap { (it) in
            if it.isSuccessful {
                return it.readJsonDebug()
            } else {
                return Single.error(HttpResponseException(it))
            }
        }
    }
    public func readJson<T: Codable>(_ type: T.Type) -> Single<T> {
        return self.flatMap { (it) in
            if it.isSuccessful {
                return it.readJson()
            } else {
                return Single.error(HttpResponseException(it))
            }
        }
    }
    //--- Single<@swiftExactly("Element")HttpResponse>.readText()
    public func readText() -> Single<String> {
        return self.flatMap { (it) in
            if it.isSuccessful {
                return Single.just(String(data: it.data, encoding: .utf8)!)
            } else {
                return Single.error(HttpResponseException(it))
            }
        }
    }
    //--- Single<@swiftExactly("Element")HttpResponse>.readData()
    public func readData() -> Single<Data> {
        return self.flatMap { (it) in
            if it.isSuccessful {
                return Single.just(it.data)
            } else {
                return Single.error(HttpResponseException(it))
            }
        }
    }
}

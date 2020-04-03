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
 
 
 

//--- Single<@swiftExactly("Element")HttpResponse>.readJson()
extension PrimitiveSequence where Element == HttpResponse, Trait == SingleTrait {
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
}

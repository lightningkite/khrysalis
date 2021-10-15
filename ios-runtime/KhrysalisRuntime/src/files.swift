import Foundation
import RxSwift

public extension URL {
    func readData() -> Single<Data> {
        let urlObj = self
       var single = Single.create { (emitter: SingleEmitter<Data>) in
            let sessionConfig = URLSessionConfiguration.default
            let session = URLSession(configuration: sessionConfig)
            var request = URLRequest(url: urlObj)

            let completionHandler = { [session] (data:Data?, response:URLResponse?, error:Error?) in
                let _ = session //We hold on this to ensure it doesn't get deinited
                if let data = data {
                    emitter.onSuccess(data)
                } else if let error = error {
                    emitter.onError(error)
                } else {
                    emitter.onError(IllegalStateException("Response is not URLResponse"))
                }
            }
                session.dataTask(with: request, completionHandler: completionHandler).resume()
        }
        if let io = HttpClient.INSTANCE.ioScheduler {
            single = single.subscribeOn(io)
        }
        if let resp = HttpClient.INSTANCE.responseScheduler {
            single = single.observeOn(resp)
        }
        return single
    }
}

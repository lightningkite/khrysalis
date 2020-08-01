
import Foundation

//--- HttpResponse
public struct HttpResponse {
    let response: HTTPURLResponse
    let data: Data
    public init(response: HTTPURLResponse, data: Data) {
        self.response = response
        self.data = data
    }

    //--- HttpResponse.code
    var code: Int {
        return Int(self.response.statusCode)
    }

    //--- HttpResponse.isSuccessful
    var isSuccessful: Bool {
        let code = self.response.statusCode
        return code >= 200 && code < 300
    }


    //--- HttpResponse.headers
    var headers: Dictionary<String, String> {
        return Dictionary(self.response.allHeaderFields
        .filter { it in it.0 is String && it.1 is String }
        .map { it in (it.key as! String, it.value as! String) }, uniquingKeysWith: { _, a in a} )
    }


    //--- HttpResponse.readText()
    func readText() -> Single<String> {
        return Single.of(String(data: data, encoding: .utf8)!)
    }


    //--- HttpResponse.readData()
    func readData() -> Single<Data> {
        return Single.of(data)
    }
    

    //--- HttpResponse.readJson()
    func readJson<T: Codable>() -> Single<T> {
        do {
            return Single.of(try T.fromJsonData(data))
        } catch (error) {
            return Single.error(error)
        }
    }

    //--- HttpResponse.readJsonDebug()
    func readJsonDebug<T: Codable>() -> Single<T> {
        do {
            print("Got response \(String(data: data, encoding: .utf8)!)")
            return Single.of(try T.fromJsonData(data))
        } catch (error) {
            return Single.error(error)
        }
    }
    

}

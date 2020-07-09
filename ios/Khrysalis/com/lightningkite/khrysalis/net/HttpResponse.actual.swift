
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
    func readText() -> String {
        return String(data: data, encoding: .utf8)!
    }
    

    //--- HttpResponse.readJson()
    func readJson<T: Codable>() -> T {
        return try! T.fromJsonData(data)
    }
    

}

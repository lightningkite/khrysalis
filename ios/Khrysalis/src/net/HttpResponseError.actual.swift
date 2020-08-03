import Foundation

public class HttpResponseException : Exception {
    public let response: HttpResponse
    public init (_ response: HttpResponse, _ cause: Exception? = nil) {
        self.response = response
        super.init("Got response \(response.code)", cause)
    }
    public convenience init (response: HttpResponse, cause: Exception? = nil) {
        self.init(response, cause)
    }
}

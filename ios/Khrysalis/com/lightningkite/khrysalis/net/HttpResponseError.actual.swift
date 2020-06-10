import Foundation
//--- HttpReadResponseException.{
//--- HttpReadResponseException.Primary Constructor
//--- HttpReadResponseException.}
//--- HttpResponseException.{
public enum HttpResponseExceptions: Error {
    case Exception(response: HttpResponse, cause: Error? = nil)
    case ReadException(response: HttpResponse, text: String, cause: Error? = nil)

    //--- HttpResponseException.Primary Constructor
    //--- HttpResponseException.}
}

func HttpResponseException(_ response: HttpResponse, _ cause: Error? = nil) -> HttpResponseExceptions {
    return HttpResponseExceptions.Exception(response: response, cause: cause)
}
func HttpReadResponseException(_ response: HttpResponse, _ text: String, _ cause: Error? = nil) -> HttpResponseExceptions {
    return HttpResponseExceptions.Exception(response: response, text: text, cause: cause)
}
func HttpResponseException(response: HttpResponse, cause: Error? = nil) -> HttpResponseExceptions {
    return HttpResponseExceptions.Exception(response: response, cause: cause)
}
func HttpReadResponseException(response: HttpResponse, text: String, cause: Error? = nil) -> HttpResponseExceptions {
    return HttpResponseExceptions.Exception(response: response, text: text, cause: cause)
}

private func thingThatThrowsError() throws {
    
}
private func test(){
    do {
        try thingThatThrowsError()
    } catch HttpResponseExceptions.Exception(let response, let cause) {
        
    } catch {
        
    }
}

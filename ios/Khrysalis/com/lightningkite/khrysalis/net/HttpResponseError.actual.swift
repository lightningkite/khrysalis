import Foundation

//--- HttpResponseException.{
public enum HttpResponseExceptions: Error {
    case Exception(response: HttpResponse, cause: Error? = nil)
    
    //--- HttpResponseException.Primary Constructor
    //--- HttpResponseException.}
}

func HttpResponseException(_ response: HttpResponse, _ cause: Error? = nil) -> HttpResponseExceptions {
    return HttpResponseExceptions.Exception(response: response, cause: cause)
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
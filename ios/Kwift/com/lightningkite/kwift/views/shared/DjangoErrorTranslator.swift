//Package: com.lightningkite.kwift.views.shared
//Converted using Kwift2

import Foundation



public class DjangoErrorTranslator {
    
    public var connectivityErrorResource: StringResource
    public var serverErrorResource: StringResource
    public var otherErrorResource: StringResource
    
    
    public func handleNode(builder: StringBuilder, node: Any?) -> Void {
        if node == nil {
            return
        }
        switch node {
        case let node as JsonMap:
            
            for (key, value) in node {
                handleNode(builder, value)
            }
        case let node as JsonList:
            
            for value in node {
                handleNode(builder, value)
            }
        case let node as String:
            if node.isNotEmpty(), node[ 0 ].isUpperCase(), node.contains(" ") {
                builder.appendln(node)
            }
        default: break
        }
    }
    public func handleNode(_ builder: StringBuilder, _ node: Any?) -> Void {
        return handleNode(builder: builder, node: node)
    }
    
    public func parseError(code: Int32, error: String?) -> ViewString?  {
        var resultError: ViewString?  = nil
        switch code / 100 {
        case 0: resultError = ViewStringResource(connectivityErrorResource)
        case 1: fallthrough
        case 2: fallthrough
        case 3: break
        case 4:
            var errorJson = error?.fromJsonStringUntyped()
            if let errorJson = errorJson {
                var builder = StringBuilder()
                handleNode(builder, errorJson)
                resultError = ViewStringRaw(builder.toString())
            } else {
                resultError = ViewStringRaw(error ?? "")
            }
        case 5: resultError = ViewStringResource(serverErrorResource)
        default: resultError = ViewStringResource(otherErrorResource)
        }
        return resultError
    }
    public func parseError(_ code: Int32, _ error: String?) -> ViewString?  {
        return parseError(code: code, error: error)
    }
    
    public func wrap<T>(callback: @escaping (_ result: T?, _ error: ViewString?) -> Void) -> (_ code: Int32, _ result: T?, _ error: String?) -> Void {
        return { (code, result, error) in 
            callback(result, self.parseError(code, error))
        }
    }
    
    public func wrapNoResponse(callback: @escaping (_ error: ViewString?) -> Void) -> (_ code: Int32, _ error: String?) -> Void {
        return { (code, error) in 
            callback(self.parseError(code, error))
        }
    }
    
    public init(connectivityErrorResource: StringResource, serverErrorResource: StringResource, otherErrorResource: StringResource) {
        self.connectivityErrorResource = connectivityErrorResource
        self.serverErrorResource = serverErrorResource
        self.otherErrorResource = otherErrorResource
    }
    convenience public init(_ connectivityErrorResource: StringResource, _ serverErrorResource: StringResource, _ otherErrorResource: StringResource) {
        self.init(connectivityErrorResource: connectivityErrorResource, serverErrorResource: serverErrorResource, otherErrorResource: otherErrorResource)
    }
}
 

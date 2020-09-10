//Package: com.lightningkite.khrysalis.net
//Converted using Khrysalis2

import Foundation
import RxSwift
import RxRelay



public class WebSocketFrame: KStringable {
    
    public var binary: Data? 
    public var text: String? 
    
    
    public func toString() -> String {
        return text ?? { () in 
            if let it = (binary) {
                return "<Binary data length \(it.count)"
            }
            return nil
        }() ?? "<Empty Frame>"
    }
    
    public init(binary: Data?  = nil, text: String?  = nil) {
        self.binary = binary
        self.text = text
    }
    convenience public init(_ binary: Data? , _ text: String?  = nil) {
        self.init(binary: binary, text: text)
    }
}
 

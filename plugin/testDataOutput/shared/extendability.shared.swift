//Package: com.test
//Converted using Khrysalis2

import Foundation
import RxSwift
import RxRelay



open class AbstractBoi {
    
    
    open var dumb: Int32 { get { fatalError() } }
    
    public init() {
    }
}
 
 

public class NonAbstract: AbstractBoi, Equatable, Hashable {
    
    public var something: Int32
    
    public static func == (lhs: NonAbstract, rhs: NonAbstract) -> Bool {
        return lhs.something == rhs.something
    }
    public func hash(into hasher: inout Hasher) {
        hasher.combine(something)
    }
    public func copy(
        something: (Int32)? = nil
    ) -> NonAbstract {
        return NonAbstract(
            something: something ?? self.something
        )
    }
    
    override public var dumb: Int32 {
        get {
            return something
        }
    }
    
    public init(something: Int32) {
        self.something = something
        super.init()
    }
    convenience public init(_ something: Int32) {
        self.init(something: something)
    }
}
 
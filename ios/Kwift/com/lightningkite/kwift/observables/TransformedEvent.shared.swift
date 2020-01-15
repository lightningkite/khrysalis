//Package: com.lightningkite.kwift.observables
//Converted using Kwift2

import Foundation



public class TransformedEvent<A, B>: Event<B> {
    
    public var basedOn: Event<A>
    public var transformation:  (A) -> B
    
    
    override public func add(listener: @escaping (B) -> Bool) -> Close {
        var transformation = self.transformation
        return basedOn.add{ (it) in 
            listener(transformation(it))
        }
    }
    
    public init(basedOn: Event<A>, transformation: @escaping (A) -> B) {
        self.basedOn = basedOn
        self.transformation = transformation
        super.init()
    }
    convenience public init(_ basedOn: Event<A>, _ transformation: @escaping (A) -> B) {
        self.init(basedOn: basedOn, transformation: transformation)
    }
}
 

extension Event {
     public func transformed<B>(transformation: @escaping (T) -> B) -> Event<B> {
        return TransformedEvent<T, B>(self, transformation)
    }
}
 
 

extension Event {
    public func map<B>(transformation: @escaping (T) -> B) -> Event<B> {
        return TransformedEvent<T, B>(self, transformation)
    }
}
 

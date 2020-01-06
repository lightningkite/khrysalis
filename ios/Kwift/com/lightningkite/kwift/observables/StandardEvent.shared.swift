//Package: com.lightningkite.kwift.observables
//Converted using Kwift2

import Foundation



public class StandardEvent<T>: Event<T> {
    
    
    public var subscriptions: Array<Subscription<T>>
    public var nextIndex: Int32
    
    public class Subscription<T> {
        
        public var listener:  (T) -> Bool
        public var identifier: Int32
        
        
        public init(listener: @escaping (T) -> Bool, identifier: Int32 = 0) {
            self.listener = listener
            self.identifier = identifier
        }
        convenience public init(_ listener: @escaping (T) -> Bool, _ identifier: Int32 = 0) {
            self.init(listener: listener, identifier: identifier)
        }
    }
    
    override public func add(listener: @escaping (T) -> Bool) -> Close {
        var thisIdentifier = nextIndex
        nextIndex += 1
        var element = Subscription(listener: { (item:T) in 
            listener(item)
            return false
        }, identifier: thisIdentifier)
        subscriptions.add(element)
        return Close(captureWeak(self) { (self) in 
            self.subscriptions.removeAll{ (it) in 
                it.identifier == thisIdentifier
            }
            return ()
        })
    }
    
    public func invokeAll(value: T) -> Void {
        subscriptions.removeAll{ (it) in 
            it.listener(value)
        }
    }
    public func invokeAll(_ value: T) -> Void {
        return invokeAll(value: value)
    }
    
    override public init() {
        let subscriptions: Array<Subscription<T>> = Array<Subscription<T>>()
        self.subscriptions = subscriptions
        let nextIndex: Int32 = 0
        self.nextIndex = nextIndex
        super.init()
    }
}
 

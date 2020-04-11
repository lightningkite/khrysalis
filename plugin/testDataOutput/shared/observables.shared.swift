//Package: com.lightningkite.khrysalis.observables.shared
//Converted using Khrysalis2

import Foundation
import RxSwift
import RxRelay



public func captureWeak<Z: AnyObject>(capture: Z, lambda: @escaping (Z) -> Void) -> () -> Void {
    weak var captured = capture
    return { () in 
        var actualCaptured = captured
        if actualCaptured == nil {
            return
        }
        lambda(actualCaptured!)
    }
}
public func captureWeak<Z: AnyObject>(_ capture: Z, _ lambda: @escaping (Z) -> Void) -> () -> Void {
    return captureWeak(capture: capture, lambda: lambda)
}
 
 

public func captureWeak<Z: AnyObject, A>(capture: Z, lambda: @escaping (Z, A) -> Void) -> (A) -> Void {
    weak var captured = capture
    return { (a) in 
        var actualCaptured = captured
        if actualCaptured == nil {
            return
        }
        lambda(actualCaptured!, a)
    }
}
public func captureWeak<Z: AnyObject, A>(_ capture: Z, _ lambda: @escaping (Z, A) -> Void) -> (A) -> Void {
    return captureWeak(capture: capture, lambda: lambda)
}
 
 

open class Event<T> {
    
    
    
    open func add(listener: @escaping (T) -> Bool) -> Close { fatalError() }
    
    public init() {
    }
}
 
 

open class ObservableProperty<T> {
    
    
    open var value: T { get { fatalError() } }
    open var onChange: Event<T> { get { fatalError() } }
    
    public init() {
    }
}
 
 

open class MutableObservableProperty<T>: ObservableProperty<T> {
    
    
    override open var value: T { get { fatalError() } set(value) { fatalError()  } }
    
    override public init() {
        super.init()
    }
}
 
 

public typealias Close = () -> Void
 
 

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
        var element = Subscription(listener: { (item: T) in 
            listener(item)
            return false
        }, identifier: thisIdentifier)
        subscriptions.add(element)
        return captureWeak(self){ (self) in 
            self.subscriptions.removeAll{ (it) in 
                it.identifier == thisIdentifier
            }
            return ()
        }
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
        let subscriptions: Array<Subscription<T>> = Array(Subscription<T>.self)
        self.subscriptions = subscriptions
        let nextIndex: Int32 = 0
        self.nextIndex = nextIndex
        super.init()
    }
}
 
 

public class StandardObservableProperty<T>: MutableObservableProperty<T> {
    
    public var underlyingValue: T
    
    override public var onChange: Event<T> { get { return _onChange } set(value) { _onChange = value } }
    override public var value: T {
        get {
            return underlyingValue
        }
        set(value) {
            underlyingValue = value
            onChange.invokeAll(value: value)
        }
    }
    
    public init(underlyingValue: T) {
        self.underlyingValue = underlyingValue
        self._onChange = StandardEvent(T.self)
        super.init()
    }
    convenience public init(_ underlyingValue: T) {
        self.init(underlyingValue: underlyingValue)
    }
    private var _onChange: Event<T>
}
 

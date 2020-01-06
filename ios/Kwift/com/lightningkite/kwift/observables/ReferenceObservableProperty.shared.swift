//Package: com.lightningkite.kwift.observables
//Converted using Kwift2

import Foundation



public class ReferenceObservableProperty<T>: MutableObservableProperty<T> {
    
    public var get:  () -> T
    public var set:  (T) -> Void
    public var event: Event<T>
    
    override public var onChange: Event<T> {
        get {
            return event
        }
    }
    override public var value: T {
        get {
            return self.get()
        }
        set(value) {
            self.set(value)
        }
    }
    
    override public func update() -> Void {
    }
    
    public init(get: @escaping () -> T, set: @escaping (T) -> Void, event: Event<T>) {
        self.get = get
        self.set = set
        self.event = event
        super.init()
    }
    convenience public init(_ get: @escaping () -> T, _ set: @escaping (T) -> Void, _ event: Event<T>) {
        self.init(get: get, set: set, event: event)
    }
}
 

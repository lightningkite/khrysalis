//Package: com.lightningkite.khrysalis.observables
//Converted using Khrysalis2

import Foundation
import RxSwift
import RxRelay



public class ReferenceObservableProperty<T>: MutableObservableProperty<T> {
    
    public var get:  () -> T
    public var set:  (T) -> Void
    public var event: Observable<Box<T>>
    
    override public var onChange: Observable<Box<T>> {
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
    
    public init(get: @escaping () -> T, set: @escaping (T) -> Void, event: Observable<Box<T>>) {
        self.get = get
        self.set = set
        self.event = event
        super.init()
    }
    convenience public init(_ get: @escaping () -> T, _ set: @escaping (T) -> Void, _ event: Observable<Box<T>>) {
        self.init(get: get, set: set, event: event)
    }
}
 

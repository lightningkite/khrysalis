//Package: com.lightningkite.khrysalis.observables
//Converted using Khrysalis2

import Foundation
import RxSwift
import RxRelay



public class EventToObservableProperty<T>: ObservableProperty<T> {
    
    public var _value: T
    override public var value: T { get { return _value } set(value) { _value = value } }
    public var wrapped: Observable<T>
    
    override public var onChange: Observable<T> {
        get {
            return wrapped.map{ (it) in 
                self.value = it.value
                return it
            }
        }
    }
    
    public init(value: T, wrapped: Observable<T>) {
        self._value = value
        self.wrapped = wrapped
        super.init()
    }
    convenience public init(_ value: T, _ wrapped: Observable<T>) {
        self.init(value: value, wrapped: wrapped)
    }
}
 

extension Observable {
    public func asObservableProperty(defaultValue: Element) -> ObservableProperty<Element> {
        return EventToObservableProperty<Element>(defaultValue, self)
    }
    public func asObservableProperty(_ defaultValue: Element) -> ObservableProperty<Element> {
        return asObservableProperty(defaultValue: defaultValue)
    }
}
 
 

extension Observable {
    public func asObservablePropertyDefaultNull() -> ObservableProperty<Element?> {
        return EventToObservableProperty<Element?>(nil, self)
    }
}
 
 

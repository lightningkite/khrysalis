//Package: com.lightningkite.kwift.observables
//Converted using Kwift2

import Foundation



public class StandardObservableProperty<T>: MutableObservableProperty<T> {
    
    public var underlyingValue: T
    public var _onChange: InvokableEvent<T>
    override public var onChange: InvokableEvent<T> { get { return _onChange } set(value) { _onChange = value } }
    
    override public var value: T {
        get {
            return underlyingValue
        }
        set(value) {
            underlyingValue = value
            onChange.invokeAll(value: value)
        }
    }
    
    override public func update() -> Void {
        onChange.invokeAll(value: value)
    }
    
    public init(underlyingValue: T, onChange: InvokableEvent<T> = StandardEvent<T>()) {
        self.underlyingValue = underlyingValue
        self._onChange = onChange
        super.init()
    }
    convenience public init(_ underlyingValue: T, _ onChange: InvokableEvent<T> = StandardEvent<T>()) {
        self.init(underlyingValue: underlyingValue, onChange: onChange)
    }
}
 

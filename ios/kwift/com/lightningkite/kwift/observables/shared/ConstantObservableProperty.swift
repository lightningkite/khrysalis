//Package: com.lightningkite.kwift.observables.shared
//Converted using Kwift2

import Foundation



public class ConstantObservableProperty<T>: ObservableProperty<T> {
    
    public var underlyingValue: T
    
    override public var onChange: NeverEvent<T> { get { return _onChange } set(value) { _onChange = value } }
    override public var value: T {
        get {
            return underlyingValue
        }
    }
    
    public init(underlyingValue: T) {
        self.underlyingValue = underlyingValue
        self._onChange = NeverEvent<T>()
        super.init()
    }
    convenience public init(_ underlyingValue: T) {
        self.init(underlyingValue: underlyingValue)
    }
    private var _onChange: NeverEvent<T>
}
 

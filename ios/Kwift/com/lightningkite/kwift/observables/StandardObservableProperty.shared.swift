//Package: com.lightningkite.kwift.observables
//Converted using Kwift2

import Foundation
import RxSwift
import RxRelay



public class StandardObservableProperty<T>: MutableObservableProperty<T> {
    
    public var underlyingValue: T
    public var _onChange: Subject<Box<T>>
    override public var onChange: Subject<Box<T>> { get { return _onChange } set(value) { _onChange = value } }
    
    override public var value: T {
        get {
            return underlyingValue
        }
        set(value) {
            underlyingValue = value
            onChange.onNext(boxWrap(value))
        }
    }
    
    override public func update() -> Void {
        onChange.onNext(boxWrap(value))
    }
    
    public init(underlyingValue: T, onChange: Subject<Box<T>> = PublishSubject.create()) {
        self.underlyingValue = underlyingValue
        self._onChange = onChange
        super.init()
    }
    convenience public init(_ underlyingValue: T, _ onChange: Subject<Box<T>> = PublishSubject.create()) {
        self.init(underlyingValue: underlyingValue, onChange: onChange)
    }
}
 

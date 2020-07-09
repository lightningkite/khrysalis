//Package: com.lightningkite.khrysalis.observables
//Converted using Khrysalis2

import Foundation
import RxSwift
import RxRelay



public class StandardObservableProperty<T>: MutableObservableProperty<T> {
    
    public var underlyingValue: T
    public var _onChange: Subject<T>
    override public var onChange: Subject<T> { get { return _onChange } set(value) { _onChange = value } }
    
    override public var value: T {
        get {
            return underlyingValue
        }
        set(value) {
            underlyingValue = value
            onChange.onNext(value)
        }
    }
    
    override public func update() -> Void {
        onChange.onNext(value)
    }
    
    public init(underlyingValue: T, onChange: Subject<T> = PublishSubject.create()) {
        self.underlyingValue = underlyingValue
        self._onChange = onChange
        super.init()
    }
    convenience public init(_ underlyingValue: T, _ onChange: Subject<T> = PublishSubject.create()) {
        self.init(underlyingValue: underlyingValue, onChange: onChange)
    }
}
 

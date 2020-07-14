// Generated by Khrysalis Swift converter - this file will be overwritten.
// File: observables/ConstantObservableProperty.shared.kt
// Package: com.lightningkite.khrysalis.observables
import RxSwift
import Foundation

public class ConstantObservableProperty<T> : ObservableProperty<T> {
    public var underlyingValue: T
    public init(underlyingValue: T) {
        self.underlyingValue = underlyingValue
        self._onChange = Observable.never()
        super.init()
    }
    
    public var _onChange: Observable<T>
    override public var onChange: Observable<T> {
        get { return _onChange }
    }
    override public var value: T {
        get { return self.underlyingValue }
    }
}



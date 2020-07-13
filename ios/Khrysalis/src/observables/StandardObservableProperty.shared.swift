// Generated by Khrysalis Swift converter - this file will be overwritten.
// File: observables/StandardObservableProperty.shared.kt
// Package: com.lightningkite.khrysalis.observables
import RxSwift
import Foundation

public class StandardObservableProperty<T> : MutableObservableProperty<T> {
    public var underlyingValue: T
    private var _onChange: Subject<T>
    override public var onChange: Subject<T> { get { return self.onChange } set(value) { self.onChange = value } }
    public init(underlyingValue: T, onChange: Subject<T> = PublishSubject()) {
        self.underlyingValue = underlyingValue
        self._onChange = onChange
        super.init()
    }
    
    override public var value: T {
        get { return self.underlyingValue }
        set(value) {
            self.underlyingValue = value
            self.onChange.onNext(value)
        }
    }
    
    override public func update() -> Void {
        self.onChange.onNext(self.value)
    }
}


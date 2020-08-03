// Generated by Khrysalis Swift converter - this file will be overwritten.
// File: observables/TransformedMutableObservableProperty2.shared.kt
// Package: com.lightningkite.khrysalis.observables
import RxSwift
import Foundation

public class TransformedMutableObservableProperty2<A, B> : MutableObservableProperty<B> {
    public var basedOn: MutableObservableProperty<A>
    public var read:  (A) -> B
    public var write:  (A, B) -> A
    public init(basedOn: MutableObservableProperty<A>, read: @escaping  (A) -> B, write: @escaping  (A, B) -> A) {
        self.basedOn = basedOn
        self.read = read
        self.write = write
        super.init()
        //Necessary properties should be initialized now
        self._onChange = self.basedOn.onChange.map({ (it: A) -> B in self.read(it) })
    }
    
    override public func update() -> Void {
        self.basedOn.update()
    }
    
    override public var value: B {
        get {
            return self.read(self.basedOn.value)
        }
        set(value) {
            self.basedOn.value = self.write(self.basedOn.value, value)
        }
    }
    public var _onChange: (Observable<B>)!
    override public var onChange: Observable<B> {
        get { return _onChange }
    }
}

public extension MutableObservableProperty {
    func mapWithExisting<B>(read: @escaping  (T) -> B, write: @escaping  (T, B) -> T) -> MutableObservableProperty<B> {
        return (TransformedMutableObservableProperty2(basedOn: self as MutableObservableProperty<T>, read: read as (T) -> B, write: write as (T, B) -> T) as TransformedMutableObservableProperty2<T, B>)
    }
}



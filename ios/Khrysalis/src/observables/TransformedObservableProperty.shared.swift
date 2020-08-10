// Generated by Khrysalis Swift converter - this file will be overwritten.
// File: observables/TransformedObservableProperty.shared.kt
// Package: com.lightningkite.khrysalis.observables
import RxSwift
import Foundation

public class TransformedObservableProperty<A, B> : ObservableProperty<B> {
    public var basedOn: ObservableProperty<A>
    public var read:  (A) -> B
    public init(basedOn: ObservableProperty<A>, read: @escaping  (A) -> B) {
        self.basedOn = basedOn
        self.read = read
        super.init()
        //Necessary properties should be initialized now
        self._onChange = self.basedOn.onChange.map({ (it) -> B in self.read(it) })
    }
    
    override public var value: B {
        get {
            return self.read(self.basedOn.value)
        }
    }
    public var _onChange: (Observable<B>)!
    override public var onChange: Observable<B> {
        get { return _onChange }
    }
}

public extension ObservableProperty {
    func transformed<B>(read: @escaping  (T) -> B) -> ObservableProperty<B> {
        return (TransformedObservableProperty(basedOn: self as ObservableProperty<T>, read: read as (T) -> B) as TransformedObservableProperty<T, B>)
    }
}

public extension ObservableProperty {
    func map<B>(read: @escaping  (T) -> B) -> ObservableProperty<B> {
        return (TransformedObservableProperty(basedOn: self as ObservableProperty<T>, read: read as (T) -> B) as TransformedObservableProperty<T, B>)
    }
}



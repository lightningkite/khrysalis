//Package: com.lightningkite.khrysalis.observables
//Converted using Khrysalis2

import Foundation
import RxSwift
import RxRelay



public class TransformedMutableObservableProperty<A, B>: MutableObservableProperty<B> {
    
    public var basedOn: MutableObservableProperty<A>
    public var read:  (A) -> B
    public var write:  (B) -> A
    
    
    override public func update() -> Void {
        basedOn.update()
    }
    override public var value: B {
        get {
            return read(basedOn.value)
        }
        set(value) {
            basedOn.value = write(value)
        }
    }
    override public var onChange: Observable<B> { get { return _onChange } set(value) { _onChange = value } }
    
    public init(basedOn: MutableObservableProperty<A>, read: @escaping (A) -> B, write: @escaping (B) -> A) {
        self.basedOn = basedOn
        self.read = read
        self.write = write
        self._onChange = basedOn.onChange.map{ (it) in
            read(it)
        }
        super.init()
    }
    convenience public init(_ basedOn: MutableObservableProperty<A>, _ read: @escaping (A) -> B, _ write: @escaping (B) -> A) {
        self.init(basedOn: basedOn, read: read, write: write)
    }
    private var _onChange: Observable<B>
}
 
 

extension MutableObservableProperty {
     public func transformed<B>(read: @escaping (T) -> B, write: @escaping (B) -> T) -> MutableObservableProperty<B> {
        return TransformedMutableObservableProperty<T, B>(self, read, write)
    }
     public func transformed<B>(_ read: @escaping (T) -> B, _ write: @escaping (B) -> T) -> MutableObservableProperty<B> {
        return transformed(read: read, write: write)
    }
}
 
 

extension MutableObservableProperty {
    public func map<B>(read: @escaping (T) -> B, write: @escaping (B) -> T) -> MutableObservableProperty<B> {
        return TransformedMutableObservableProperty<T, B>(self, read, write)
    }
    public func map<B>(_ read: @escaping (T) -> B, _ write: @escaping (B) -> T) -> MutableObservableProperty<B> {
        return map(read: read, write: write)
    }
}
 

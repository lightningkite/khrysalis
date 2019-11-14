//Package: com.lightningkite.kwift.observables.shared
//Converted using Kwift2

import Foundation



public class TransformedMutableObservableProperty2<A, B>: MutableObservableProperty<B> {
    
    public var basedOn: MutableObservableProperty<A>
    public var read:  (A) -> B
    public var write:  (A, B) -> A
    
    
    override public func update() -> Void {
        basedOn.update()
    }
    override public var value: B {
        get {
            return read(basedOn.value)
        }
        set(value) {
            basedOn.value = write(basedOn.value, value)
        }
    }
    override public var onChange: Event<B> { get { return _onChange } set(value) { _onChange = value } }
    
    public init(basedOn: MutableObservableProperty<A>, read: @escaping (A) -> B, write: @escaping (A, B) -> A) {
        self.basedOn = basedOn
        self.read = read
        self.write = write
        self._onChange = basedOn.onChange.transformed(transformation: read)
        super.init()
    }
    convenience public init(_ basedOn: MutableObservableProperty<A>, _ read: @escaping (A) -> B, _ write: @escaping (A, B) -> A) {
        self.init(basedOn: basedOn, read: read, write: write)
    }
    private var _onChange: Event<B>
}
 
 

extension MutableObservableProperty {
    public func mapWithExisting<B>(read: @escaping (T) -> B, write: @escaping (T, B) -> T) -> MutableObservableProperty<B> {
        return TransformedMutableObservableProperty2<T, B>(self, read, write)
    }
    public func mapWithExisting<B>(_ read: @escaping (T) -> B, _ write: @escaping (T, B) -> T) -> MutableObservableProperty<B> {
        return mapWithExisting(read: read, write: write)
    }
}
 

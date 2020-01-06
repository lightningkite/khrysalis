//Package: com.lightningkite.kwift.observables
//Converted using Kwift2

import Foundation



public class WriteAddedObservableProperty<A>: MutableObservableProperty<A> {
    
    public var basedOn: ObservableProperty<A>
    public var onWrite:  (A) -> Void
    
    
    override public func update() -> Void {
        onWrite(basedOn.value)
    }
    override public var value: A {
        get {
            return basedOn.value
        }
        set(value) {
            onWrite(value)
        }
    }
    override public var onChange: Event<A> {
        get {
            return basedOn.onChange
        }
    }
    
    public init(basedOn: ObservableProperty<A>, onWrite: @escaping (A) -> Void) {
        self.basedOn = basedOn
        self.onWrite = onWrite
        super.init()
    }
    convenience public init(_ basedOn: ObservableProperty<A>, _ onWrite: @escaping (A) -> Void) {
        self.init(basedOn: basedOn, onWrite: onWrite)
    }
}
 
 

extension ObservableProperty {
    public func withWrite(onWrite: @escaping (T) -> Void) -> MutableObservableProperty<T> {
        return WriteAddedObservableProperty<T>(self, onWrite)
    }
}
 

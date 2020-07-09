//Package: com.lightningkite.khrysalis.observables
//Converted using Khrysalis2

import Foundation
import RxSwift
import RxRelay



public class WriteAddedObservableProperty<A>: MutableObservableProperty<A> {
    
    public var basedOn: ObservableProperty<A>
    public var onWrite:  (A) -> Void
    
    override public var value: A {
        get {
            return basedOn.value
        }
        set(value) {
            onWrite(value)
        }
    }
    override public var onChange: Observable<A> {
        get {
            return basedOn.onChange
        }
    }
    
    override public func update() -> Void {
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
 

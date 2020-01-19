//Package: com.lightningkite.kwift.observables
//Converted using Kwift2

import Foundation
import RxSwift
import RxRelay



public class FlatMappedObservableProperty<A, B>: ObservableProperty<B> {
    
    public var basedOn: ObservableProperty<A>
    public var transformation:  (A) -> ObservableProperty<B>
    
    override public var value: B {
        get {
            return transformation(basedOn.value).value
        }
    }
    override public var onChange: Observable<Box<B>> {
        get {
            return basedOn.onChange.flatMap{ (it) in 
                self.transformation(it.value).onChange
            }
        }
    }
    
    public init(basedOn: ObservableProperty<A>, transformation: @escaping (A) -> ObservableProperty<B>) {
        self.basedOn = basedOn
        self.transformation = transformation
        super.init()
    }
    convenience public init(_ basedOn: ObservableProperty<A>, _ transformation: @escaping (A) -> ObservableProperty<B>) {
        self.init(basedOn: basedOn, transformation: transformation)
    }
}
 
 

extension ObservableProperty {
    public func flatMap<B>(transformation: @escaping (T) -> ObservableProperty<B>) -> FlatMappedObservableProperty<T, B> {
        return FlatMappedObservableProperty<T, B>(self, transformation)
    }
}
 
 

public class MutableFlatMappedObservableProperty<A, B>: MutableObservableProperty<B> {
    
    public var basedOn: ObservableProperty<A>
    public var transformation:  (A) -> MutableObservableProperty<B>
    
    override public var value: B {
        get {
            return transformation(basedOn.value).value
        }
        set(value) {
            transformation(basedOn.value).value = value
        }
    }
    public var lastProperty: MutableObservableProperty<B>? 
    override public var onChange: Observable<Box<B>> {
        get {
            return basedOn.onChange.flatMap  { (it:Box<A>) -> Observable<Box<B>> in 
                var prop = self.transformation(it.value)
                self.lastProperty = prop
                return prop.onChange
            }
        }
    }
    
    override public func update() -> Void {
        lastProperty?.update()
    }
    
    public init(basedOn: ObservableProperty<A>, transformation: @escaping (A) -> MutableObservableProperty<B>) {
        self.basedOn = basedOn
        self.transformation = transformation
        let lastProperty: MutableObservableProperty<B>?  = nil
        self.lastProperty = lastProperty
        super.init()
    }
    convenience public init(_ basedOn: ObservableProperty<A>, _ transformation: @escaping (A) -> MutableObservableProperty<B>) {
        self.init(basedOn: basedOn, transformation: transformation)
    }
}
 
 

extension ObservableProperty {
    public func flatMapMutable<B>(transformation: @escaping (T) -> MutableObservableProperty<B>) -> MutableFlatMappedObservableProperty<T, B> {
        return MutableFlatMappedObservableProperty<T, B>(self, transformation)
    }
}
 

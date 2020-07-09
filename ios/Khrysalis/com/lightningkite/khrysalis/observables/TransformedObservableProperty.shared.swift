//Package: com.lightningkite.khrysalis.observables
//Converted using Khrysalis2

import Foundation
import RxSwift
import RxRelay



public class TransformedObservableProperty<A, B>: ObservableProperty<B> {
    
    public var basedOn: ObservableProperty<A>
    public var read:  (A) -> B
    
    override public var value: B {
        get {
            return read(basedOn.value)
        }
    }
    override public var onChange: Observable<B> { get { return _onChange } set(value) { _onChange = value } }
    
    public init(basedOn: ObservableProperty<A>, read: @escaping (A) -> B) {
        self.basedOn = basedOn
        self.read = read
        self._onChange = basedOn.onChange.map{ (it) in 
            read(it)
        }
        super.init()
    }
    convenience public init(_ basedOn: ObservableProperty<A>, _ read: @escaping (A) -> B) {
        self.init(basedOn: basedOn, read: read)
    }
    private var _onChange: Observable<B>
}
 

extension ObservableProperty {
     public func transformed<B>(read: @escaping (T) -> B) -> ObservableProperty<B> {
        return TransformedObservableProperty<T, B>(self, read)
    }
}
 
 

extension ObservableProperty {
    public func map<B>(read: @escaping (T) -> B) -> ObservableProperty<B> {
        return TransformedObservableProperty<T, B>(self, read)
    }
}
 

//Package: com.lightningkite.kwift.observables
//Converted using Kwift2

import Foundation



extension ObservableProperty {
    public func combine<B, C>(other: ObservableProperty<B>, combiner: @escaping (T, B) -> C) -> ObservableProperty<C> {
        return flatMap{ (av) in 
            other.map{ (bv) in 
                combiner(av, bv)
            }
        }
    }
    public func combine<B, C>(_ other: ObservableProperty<B>, _ combiner: @escaping (T, B) -> C) -> ObservableProperty<C> {
        return combine(other: other, combiner: combiner)
    }
}
 

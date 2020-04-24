//Package: com.lightningkite.khrysalis.observables.binding
//Converted using Khrysalis2

import Foundation
import Khrysalis
import RxSwift
import RxRelay



extension MutableObservableProperty {
     public func serves(whilePresent: AnyObject, other: MutableObservableProperty<T>) -> Void {
        var suppress = false
        other.observable.addWeak(whilePresent, { (ignored, value) in 
            if !suppress {
                suppress = true
                self.value = value.value
                suppress = false
            }
        })
        self.onChange.addWeak(whilePresent, { (ignored, value) in 
            if !suppress {
                suppress = true
                other.value = value.value
                suppress = false
            }
        })
    }
     public func serves(_ whilePresent: AnyObject, _ other: MutableObservableProperty<T>) -> Void {
        return serves(whilePresent: whilePresent, other: other)
    }
}
 
 

extension MutableObservableProperty {
    public func serves(until: DisposeCondition, other: MutableObservableProperty<T>) -> Void {
        var suppress = false
        other.observable.subscribeBy{ (value) in 
            if !suppress {
                suppress = true
                self.value = value.value
                suppress = false
            }
        }.until(until)
        self.onChange.subscribeBy{ (value) in 
            if !suppress {
                suppress = true
                other.value = value.value
                suppress = false
            }
        }.until(until)
    }
    public func serves(_ until: DisposeCondition, _ other: MutableObservableProperty<T>) -> Void {
        return serves(until: until, other: other)
    }
}
 
 

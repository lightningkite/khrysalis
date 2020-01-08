//Package: com.lightningkite.kwift.observables.binding
//Converted using Kwift2

import Foundation



extension MutableObservableProperty {
    public func serves(whilePresent: AnyObject, other: MutableObservableProperty<T>) -> Void {
        var suppress = false
        other.addAndRunWeak(whilePresent) { (ignored, value) in 
            if !suppress {
                suppress = true
                self.value = value
                suppress = false
            }
        }
        self.onChange.addWeak(whilePresent) { (ignored, value) in 
            if !suppress {
                suppress = true
                other.value = value
                suppress = false
            }
        }
    }
    public func serves(_ whilePresent: AnyObject, _ other: MutableObservableProperty<T>) -> Void {
        return serves(whilePresent: whilePresent, other: other)
    }
}
 

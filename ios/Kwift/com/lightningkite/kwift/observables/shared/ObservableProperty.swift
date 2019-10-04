//Package: com.lightningkite.kwift.observables.shared
//Converted using Kwift2

import Foundation



open class ObservableProperty<T> {
    
    
    open var value: T { get { fatalError() } }
    open var onChange: Event<T> { get { fatalError() } }
    
    public init() {
    }
}
 
 

//Package: com.lightningkite.kwift.observables.shared
//Converted using Kwift2

import Foundation



open class MutableObservableProperty<T>: ObservableProperty<T> {
    
    
    override open var value: T { get { fatalError() } set(value) { fatalError()  } }
    
    open func update() -> Void { fatalError() }
    
    override public init() {
        super.init()
    }
}
 

//Package: com.lightningkite.kwift.observables.shared
//Converted using Kwift2

import Foundation



public class MutableObservableProperty<T>: ObservableProperty<T> {
    
    
    override public var value: T { get { fatalError() } set(value) { fatalError()  } }
    
    override public init() {
        super.init()
    }
}
 

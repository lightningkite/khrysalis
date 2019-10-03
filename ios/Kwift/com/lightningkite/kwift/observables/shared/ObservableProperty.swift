//Package: com.lightningkite.kwift.observables.shared
//Converted using Kwift2

import Foundation



public class ObservableProperty<T> {
    
    
    public var value: T { get { fatalError() } }
    public var onChange: Event<T> { get { fatalError() } }
    
    public init() {
    }
}
 
 

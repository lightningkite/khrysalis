//Package: com.lightningkite.khrysalis.observables
//Converted using Khrysalis2

import Foundation
import Khrysalis
import RxSwift
import RxRelay



open class MutableObservableProperty<T>: ObservableProperty<T> {
    
    
    override open var value: T { get { fatalError() } set(value) { fatalError()  } }
    
    open func update() -> Void { fatalError() }
    
    override public init() {
        super.init()
    }
}
 

//Package: com.lightningkite.khrysalis.observables
//Converted using Khrysalis2

import Foundation
import RxSwift
import RxRelay



open class ObservableProperty<T> {
    
    
    open var value: T { get { fatalError() } }
    open var onChange: Observable<Box<T>> { get { fatalError() } }
    
    public init() {
    }
}
 
 

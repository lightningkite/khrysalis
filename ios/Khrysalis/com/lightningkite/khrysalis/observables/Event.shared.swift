//Package: com.lightningkite.khrysalis.observables
//Converted using Khrysalis2

import Foundation
import Khrysalis
import RxSwift
import RxRelay



public typealias Event<Element> = Observable<Element>
 

public typealias InvokableEvent<Element> = Subject<Element>
 

public typealias StandardEvent<Element> = PublishSubject<Element>
 

//Package: com.lightningkite.kwift.observables
//Converted using Kwift2

import Foundation
import RxSwift
import RxRelay



public typealias Event<Element> = Observable<Element>
 

public typealias InvokableEvent<Element> = Subject<Element>
 

public typealias StandardEvent<Element> = PublishSubject<Element>
 

import RxSwift
import RxRelay

//--- Observable<Element>.combineLatest(Observable<R>, (Element,R)->OUT)
public extension Observable {
    func combineLatest<Element, R, OUT>(_ observable: Observable<R>, _ function: (Element, R) -> OUT) -> Observable<OUT> {
        TODO()
    }
    func combineLatest<Element, R, OUT>(observable: Observable<R>, function: (Element, R) -> OUT) -> Observable<OUT> {
        return combineLatest(observable, function)
    }
}

//--- Observable<Box<Element>>.filterNotNull()
extension Observable where Observable.Element: BoxProtocol, Observable.Element.T: OptionalConvertible {
    func filterNotNull() -> Observable<T.Wrapped> {
        self.map { $0.asBox().value.asOptional }.filter { $0 != nil }.map { $0! }
    }
}

//--- Observable<Element>.mapNotNull((Element)->Destination?)
public extension Observable {
    func mapNotNull<Destination>(_ transform: @escaping (Element) -> Destination?) -> Observable<Destination> {
        return self.flatMap { (it: Element) -> Observable<Destination> in
            if let result: Destination = transform(it) {
                return Observable<Destination>.just(result)
            } else {
                return Observable<Destination>.empty()
            }
        }
    }
}







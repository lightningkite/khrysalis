import RxSwift
import RxRelay

//--- Observable<Element>.combineLatest(Observable<R>, (Element,R)->OUT)
extension ObservableType {
    /**
    Merges the specified observable sequences into one observable sequence by using the selector function whenever any of the observable sequences produces an element.
    - seealso: [combineLatest operator on reactivex.io](http://reactivex.io/documentation/operators/combinelatest.html)
    - parameter resultSelector: Function to invoke whenever any of the sources produces an element.
    - returns: An observable sequence containing the result of combining elements of the sources using the specified result selector function.
    */
    public func combineLatest<O2: ObservableType, OUT>
        (observable: O2, function: @escaping (Element, O2.Element) throws -> OUT)
            -> Observable<OUT> {
                return Observable<OUT>.combineLatest(self, observable, resultSelector: function)
    }
    public func combineLatest<O2: ObservableType, OUT>
        (_ observable: O2, _ function: @escaping (Element, O2.Element) throws -> OUT)
            -> Observable<OUT> {
                return Observable<OUT>.combineLatest(self, observable, resultSelector: function)
    }
}

//--- Observable<Element>.filterNotNull()
extension Observable where Observable.Element: OptionalConvertible {
    func filterNotNull() -> Observable<Element.Wrapped> {
        self.filter { $0.asOptional != nil }.map { $0.asOptional! }
    }
}

//--- List<Observable<IN>>.combineLatest((List<IN>)->OUT)
func xListCombineLatest<IN, OUT>(
    _ self: Array<Observable<IN>>,
    combine: @escaping (Array<IN>) -> OUT
) -> Observable<OUT> {
    return Observable.combineLatest(self, resultSelector: combine)
}
func xListCombineLatest<IN>(
    _ self: Array<Observable<IN>>
) -> Observable<Array<IN>> {
    return Observable.combineLatest(self)
}
extension Array where Element: ObservableType {
    func combineLatest<OUT>(combine: @escaping (Array<Element.Element>)->OUT) -> Observable<OUT> {
        return Observable.combineLatest(self, resultSelector: combine)
    }
    func combineLatest() -> Observable<Array<Element.Element>> {
        return Observable.combineLatest(self)
    }
}

//--- Observable<Element>.mapNotNull((Element)->Destination?)
public extension Observable {
    func mapNotNull<Destination>(_ transform: @escaping (Element) -> Destination?) -> Observable<Destination> {
        return mapNotNull(transform: transform)
    }
    func mapNotNull<Destination>(transform: @escaping (Element) -> Destination?) -> Observable<Destination> {
        return self.flatMap { (it: Element) -> Observable<Destination> in
            if let result: Destination = transform(it) {
                return Observable<Destination>.just(result)
            } else {
                return Observable<Destination>.empty()
            }
        }
    }
}

//--- Single<Element>.working(MutableObservableProperty<Boolean>)
public extension PrimitiveSequenceType where Trait == SingleTrait {
    func working(_ observable: MutableObservableProperty<Bool>) -> Single<Element> {
        return self.do(onSuccess: { _ in observable.value = false }, onError: { _ in observable.value = false }, onSubscribe: { observable.value = true })
    }
    func working(observable: MutableObservableProperty<Bool>) -> Single<Element> {
        return working(observable)
    }
}

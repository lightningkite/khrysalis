import RxSwift
import RxRelay

//--- Observable<T>.asObservablePropertyUnboxed(T)
public func xObservableAsObservablePropertyUnboxed<T>(_ obs: Observable<T>, defaultValue: T) -> ObservableProperty<T> {
    return EventToObservableProperty(value: defaultValue, wrapped: obs)
}

import RxSwift
import RxRelay

//--- Observable<Box<T>>.asObservablePropertyUnboxed(T)
extension Observable where Observable.Element: BoxProtocol {
    public typealias T = Observable.Element.T
    public func asObservablePropertyNullable(defaultValue: T) -> ObservableProperty<T> {
        return EventToObservableProperty<T>(defaultValue, self.map { it in it.asBox() })
    }
    public func asObservablePropertyNullable(_ defaultValue: T) -> ObservableProperty<T> {
        return asObservablePropertyNullable(defaultValue: defaultValue)
    }
}







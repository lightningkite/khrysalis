import RxSwift
import RxRelay

//--- Observer<Element>.invokeAll(Element)
extension ObserverType {
     public func invokeAll(value: Element) -> Void {
        return onNext(value)
    }
     public func invokeAll(_ value: Element) -> Void {
        return invokeAll(value: value)
    }
}

import UIKit
import RxSwift

public class ApplicationAccess {
    public static let INSTANCE = ApplicationAccess()

    //--- _applicationIsActive
    public let applicationIsActiveEvent = PublishSubject<Bool>()
    public let foreground: ObservableProperty<Bool>
    
    init() {
        foreground = applicationIsActiveEvent
        .debounce(.milliseconds(100), scheduler: MainScheduler.instance)
        .distinctUntilChanged()
        .asObservableProperty(defaultValue: true)
    }
    
    public let softInputActive = StandardObservableProperty<Bool>(underlyingValue: false)
}

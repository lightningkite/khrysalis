import UIKit

public class ApplicationAccess {
    public static let INSTANCE = ApplicationAccess()

    //--- _applicationIsActive
    public let applicationIsActiveEvent = PublishSubject<Bool>()
    public let foreground = applicationIsActiveEvent
        .debounce(.milliseconds(100), scheduler: MainScheduler.instance)
         .distinctUntilChanged()
        .asObservableProperty(defaultValue: true)

    public let softInputActive = StandardObservableProperty<Boolean>(false)
    //ApplicationAccess.INSTANCE.softInputActive
}
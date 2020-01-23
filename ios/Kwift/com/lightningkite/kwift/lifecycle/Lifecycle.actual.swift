import UIKit
import RxSwift


//--- View.lifecycle
public extension View {
    var lifecycle: Lifecycle {
        TODO()
        return StandardObservableProperty(false)
    }
}


//--- T.untilOff(Lifecycle)
public extension Disposable {
    func untilOff(_ lifecycle: Lifecycle) -> Self {
        lifecycle.closeWhenOff(self)
        return self
    }
    func untilOff(lifecycle: Lifecycle) -> Self {
        return untilOff(lifecycle)
    }
}

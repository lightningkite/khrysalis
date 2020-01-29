import UIKit
import RxSwift


//--- View.lifecycle
public extension View {
    private static var lifecycleAssociationKey: UInt8 = 0
    private static var lifecycleExtension: ExtensionProperty<View, StandardObservableProperty<Bool>> = ExtensionProperty()
    var lifecycle: Lifecycle {
        if let existing = View.lifecycleExtension.get(self) {
            return existing
        }
        let prop = StandardObservableProperty(true)
        View.lifecycleExtension.set(self, prop)
        objc_setAssociatedObject(self, &View.lifecycleAssociationKey, DeinitCallback {
            prop.value = false
        }, .OBJC_ASSOCIATION_RETAIN_NONATOMIC)
        return prop
    }
    
    class DeinitCallback {
        var callback: ()->Void
        init(_ callback: @escaping ()->Void){
            self.callback = callback
        }
        deinit {
            self.callback()
        }
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

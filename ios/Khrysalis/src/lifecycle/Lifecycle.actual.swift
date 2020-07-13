import UIKit
import RxSwift


//--- View.lifecycle
public extension View {
    private static var lifecycleAssociationKey: UInt8 = 0
    internal static var lifecycleExtension: ExtensionProperty<View, StandardObservableProperty<Bool>> = ExtensionProperty()
    var lifecycle: Lifecycle {
        if let existing = View.lifecycleExtension.get(self) {
            return existing
        }
        let prop = StandardObservableProperty(underlyingValue: true)
        View.lifecycleExtension.set(self, prop)
        prop.value = window != nil
        return prop
    }
    
    private class DeinitCallback {
        var callback: ()->Void
        init(_ callback: @escaping ()->Void){
            self.callback = callback
        }
        deinit {
            self.callback()
        }
    }
    
    private func connected() -> Bool {
        return self.window != nil || self.superview?.connected() ?? false
    }
}


//--- T.untilOff(Lifecycle)
public extension Disposable {
    func untilOff(_ lifecycle: Lifecycle) -> Self {
        lifecycle.closeWhenOff(closeable: self)
        return self
    }
    func untilOff(lifecycle: Lifecycle) -> Self {
        return untilOff(lifecycle)
    }
}

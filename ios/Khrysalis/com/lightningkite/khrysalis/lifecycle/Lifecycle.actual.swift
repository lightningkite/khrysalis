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
        prop.value = window != nil
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
    
    func refreshLifecycle(){
        if let prop = View.lifecycleExtension.get(self) {
            if prop.value != (window != nil) {
                prop.value = window != nil
            }
        }
        for view in self.subviews {
            view.refreshLifecycle()
        }
    }
    
    private func connected() -> Bool {
        return self.window != nil || self.superview?.connected() ?? false
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

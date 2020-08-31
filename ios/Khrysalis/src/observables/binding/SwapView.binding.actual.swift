//Stub file made with Khrysalis 2 (by Lightning Kite)
import Foundation


//--- SwapView.bindStack(ViewDependency, ObservableStack<ViewGenerator>)
public extension SwapView {
    func bindStack(_ dependency: ViewDependency, _ obs: ObservableStack<ViewGenerator>) -> Void {
        var lastCount = 0
        obs.subscribeBy { value in
            DispatchQueue.main.asyncAfter(deadline: .now() + 0.01, execute: {
                var animation = Animation.fade
                if lastCount == 0 {
                    animation = .fade
                } else if value.count > lastCount {
                    animation = .push
                } else if value.count < lastCount {
                    animation = .pop
                }
                lastCount = value.count
                if let newView = value.last?.generate(dependency: dependency) {
                    self.swap(dependency: dependency, to: newView, animation: animation)
                } else {
                    self.swap(dependency: dependency, to: nil, animation: animation)
                }
            })
        }.until(self.removed)
    }
    func bindStack(dependency: ViewDependency, obs: ObservableStack<ViewGenerator>) -> Void {
        return bindStack(dependency, obs)
    }
}

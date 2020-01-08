//Stub file made with Kwift 2 (by Lightning Kite)
import Foundation


//--- SwapView.bindStack(ViewDependency, ObservableStack<ViewGenerator>)
public extension SwapView {
    func bindStack(_ dependency: ViewDependency, _ obs: ObservableStack<ViewGenerator>) -> Void {
        var lastCount = 0
        obs.addAndRunWeak(self) { this, value in
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
                    self.swap(to: newView, animation: animation)
                } else {
                    self.swap(to: nil, animation: animation)
                }
            })
        }
    }
    func bindStack(dependency: ViewDependency, obs: ObservableStack<ViewGenerator>) -> Void {
        return bindStack(dependency, obs)
    }
}










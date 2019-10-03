//Package: com.lightningkite.kwift.views.shared
//Converted using Kwift2

import Foundation


public protocol ViewGenerator {
    
    var title: String { get }
    
    func generate(dependency: ViewDependency) -> View
    func generate(_ dependency: ViewDependency) -> View
}
 
 
 
public class MainViewGenerator: ViewGenerator {
    
    
    public var title: String {
        get {
            return "Main"
        }
    }
    public var stack: ObservableStack<ViewGenerator>
    
    public func generate(dependency: ViewDependency) -> View {
        var xml = MainXml()
        var view = xml.setup(dependency)
        xml.boundViewMainContent.bindStack(dependency, stack)
        xml.boundViewTitle.bindText(stack) { (it) in 
            (it.lastOrNull()?.title) ?? ""
        }
        xml.boundViewMainBack.bindVisible(stack.transformed{ (it) in 
            it.size > 1
        })
        xml.boundViewMainBack.onClick(captureWeak(self) { (self) in 
            self.stack.pop()
            ()
        })
        return view
    }
    public func generate(_ dependency: ViewDependency) -> View {
        return generate(dependency: dependency)
    }
    
    public init() {
        stack = ObservableStack<ViewGenerator>()
        stack.push(ExampleContentViewData(stack))
    }
}
 

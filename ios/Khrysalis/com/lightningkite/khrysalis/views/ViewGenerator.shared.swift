//Package: com.lightningkite.khrysalis.views
//Converted using Khrysalis2

import Foundation
import RxSwift
import RxRelay



open class ViewGenerator {
    
    
    open var title: String { get { fatalError() } }
    
    open func generate(dependency: ViewDependency) -> View { fatalError() }
    open func generate(_ dependency: ViewDependency) -> View { fatalError() }
    
    public class Default: ViewGenerator {
        
        
        override public var title: String {
            get {
                return "Empty"
            }
        }
        
        override public func generate(dependency: ViewDependency) -> View {
            return EmptyView(dependency)
        }
        override public func generate(_ dependency: ViewDependency) -> View {
            return generate(dependency: dependency)
        }
        
        override public init() {
            super.init()
        }
    }
    
    public init() {
    }
}
 

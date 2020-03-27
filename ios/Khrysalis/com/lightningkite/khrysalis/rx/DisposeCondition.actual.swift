import RxSwift

//--- View.removed
public extension View {
    private static var disposablesAssociationKey: UInt8 = 0
    private static var disposablesExtension: ExtensionProperty<View, Array<Disposable>> = ExtensionProperty()
    private static var beenActiveExtension: ExtensionProperty<View, Bool> = ExtensionProperty()
    private var disposables: Array<Disposable> {
        get {
            return View.disposablesExtension.getOrPut(self) { [] }
        }
        set(value) {
            View.disposablesExtension.set(self, value)
        }
    }
        
    func refreshLifecycle(){
        if let prop = View.lifecycleExtension.get(self) {
            if prop.value != (window != nil) {
                prop.value = window != nil
            }
        }
        
        let previouslyActive = View.beenActiveExtension.get(self) == true
        if !previouslyActive && window != nil {
            View.beenActiveExtension.set(self, true)
        }
        if previouslyActive && window == nil {
            disposables.forEach { $0.dispose() }
            disposables = []
        }
        
        for view in self.subviews {
            view.refreshLifecycle()
        }
    }
    
    private func connected() -> Bool {
        return self.window != nil || self.superview?.connected() ?? false
    }
    var removed: DisposeCondition {
        return DisposeCondition { it in
            self.disposables.append(it)
        }
    }
}


//--- DisposableLambda.{
public class DisposableLambda: Disposable {
    
    public var lambda:  () -> Void
    
    //--- DisposableLambda.disposed
    //--- DisposableLambda.isDisposed()
    public var disposed: Bool
    
    //--- DisposableLambda.dispose()
    public func dispose() -> Void {
        if !disposed {
            disposed = true
            lambda()
        }
    }
    
    //--- DisposableLambda.Primary Constructor
    public init(lambda: @escaping () -> Void) {
        self.lambda = lambda
        let disposed: Bool = false
        self.disposed = disposed
    }
    convenience public init(_ lambda: @escaping () -> Void) {
        self.init(lambda: lambda)
    }
    //--- DisposableLambda.}
}

 
 
//--- Self.forever()
extension Disposable {
    @discardableResult
    public func forever() -> Self {
        return self
    }
}

//--- Self.until(DisposeCondition)
extension Disposable {
    @discardableResult
    public func until(condition: DisposeCondition) -> Self {
        condition.call(self)
        return self
    }
    @discardableResult
    public func until(_ condition: DisposeCondition) -> Self {
        return until(condition: condition)
    }
}
 

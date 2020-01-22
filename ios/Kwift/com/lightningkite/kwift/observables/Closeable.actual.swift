
//--- Closeable
public protocol Closeable {
    func close()
}

//--- Close.{
public class Close: Closeable {
    
    let closer: ()->Void
    
    //--- Close.Primary Constructor
    public init(_ closer: @escaping () -> Void) {
        self.closer = closer
    }
    
    //--- Close.disposed (overwritten on flow generation)
    public var disposed: Bool {
        get {
            TODO()
        }
        set(value) {
            TODO()
        }
    }
    
    //--- Close.isDisposed() (overwritten on flow generation)
    public func isDisposed() -> Bool {
        TODO()
    }
    
    //--- Close.close()
    public func close() -> Void {
        closer()
    }
    
    //--- Close.}
}

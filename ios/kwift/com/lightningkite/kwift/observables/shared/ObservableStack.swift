//Package: com.lightningkite.kwift.observables.shared
//Converted using Kwift2

import Foundation



public class ObservableStack<T: AnyObject>: ObservableProperty<Array<T>> {
    
    
    override public var onChange: StandardEvent<Array<T>> { get { return _onChange } set(value) { _onChange = value } }
    override public var value: Array<T> {
        get {
            return stack
        }
    }
    public var stack: Array<T>
    
    public func push(t: T) -> Void {
        stack.add(t)
        onChange.invokeAll(value: stack)
    }
    public func push(_ t: T) -> Void {
        return push(t: t)
    }
    
    public func pop() -> Bool {
        if stack.size <= 1 {
            return false
        }
        stack.removeAt(stack.lastIndex)
        onChange.invokeAll(value: stack)
        return true
    }
    
    public func popTo(t: T) -> Void {
        var found = false
        
        for i in 0 ... stack.lastIndex {
            if found {
                stack.removeAt(stack.lastIndex)
            } else if stack[ i ] === t {
                found = true
            }
        }
        onChange.invokeAll(value: stack)
    }
    public func popTo(_ t: T) -> Void {
        return popTo(t: t)
    }
    
    public func root() -> Void {
        popTo(t: stack.first())
    }
    
    public func reset(t: T) -> Void {
        stack.clear()
        stack.add(t)
        onChange.invokeAll(value: stack)
    }
    public func reset(_ t: T) -> Void {
        return reset(t: t)
    }
    
    override public init() {
        self._onChange = StandardEvent<Array<T>>()
        let stack: Array<T> = Array<T>()
        self.stack = stack
        super.init()
    }
    private var _onChange: StandardEvent<Array<T>>
}
 

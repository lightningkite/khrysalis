//Package: com.lightningkite.khrysalis.observables
//Converted using Khrysalis2

import Foundation
import RxSwift
import RxRelay



public class ObservableStack<T: AnyObject>: ObservableProperty<Array<T>> {
    
    
    
    //Start Companion
    
    static public func withFirst<T: AnyObject>(value: T) -> ObservableStack<T> {
        var result = ObservableStack<T>()
        result.reset(value)
        return result
    }
    static public func withFirst<T: AnyObject>(_ value: T) -> ObservableStack<T> {
        return withFirst(value: value)
    }
    //End Companion
    
    override public var onChange: PublishSubject<Array<T>> { get { return _onChange } set(value) { _onChange = value } }
    override public var value: Array<T> {
        get {
            return stack
        }
    }
    public var stack: Array<T>
    
    public func push(t: T) -> Void {
        stack.add(t)
        onChange.onNext(stack)
    }
    public func push(_ t: T) -> Void {
        return push(t: t)
    }
    
    public func swap(t: T) -> Void {
        stack.removeAt(stack.lastIndex)
        stack.add(t)
        onChange.onNext(stack)
    }
    public func swap(_ t: T) -> Void {
        return swap(t: t)
    }
    
    public func pop() -> Bool {
        if stack.size <= 1 {
            return false
        }
        stack.removeAt(stack.lastIndex)
        onChange.onNext(stack)
        return true
    }
    
    public func dismiss() -> Bool {
        if stack.isEmpty() {
            return false
        }
        stack.removeAt(stack.lastIndex)
        onChange.onNext(stack)
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
        onChange.onNext(stack)
    }
    public func popTo(_ t: T) -> Void {
        return popTo(t: t)
    }
    
     public func popTo(predicate: (T) -> Bool) -> Void {
        var found = false
        
        for i in 0 ... stack.lastIndex {
            if found {
                stack.removeAt(stack.lastIndex)
            } else if predicate(stack[ i ]) {
                found = true
            }
        }
        onChange.onNext(stack)
    }
    
    public func root() -> Void {
        popTo(t: stack.first())
    }
    
    public func reset(t: T) -> Void {
        stack.clear()
        stack.add(t)
        onChange.onNext(stack)
    }
    public func reset(_ t: T) -> Void {
        return reset(t: t)
    }
    
    override public init() {
        self._onChange = PublishSubject.create()
        let stack: Array<T> = Array<T>()
        self.stack = stack
        super.init()
    }
    private var _onChange: PublishSubject<Array<T>>
}
 

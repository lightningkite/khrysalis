//Package: com.lightningkite.khrysalis.lifecycle
//Converted using Khrysalis2

import Foundation
import RxSwift
import RxRelay



public typealias Lifecycle = ObservableProperty<Bool>
 

extension ObservableProperty where T == Bool {
     public func and(other: ObservableProperty<Bool>) -> Lifecycle {
        return self.combine(other){ (a, b) in 
            a && b
        }
    }
     public func and(_ other: ObservableProperty<Bool>) -> Lifecycle {
        return and(other: other)
    }
}

extension ObservableProperty where T == Bool {
     public func openCloseBinding<A: AnyObject>(target: A, open: @escaping (A) -> Void, close: @escaping (A) -> Void) -> Void {
        var lastValue = self.value
        if self.value {
            open(target)
        }
        self.addAndRunWeak(target){ (target, value) in 
            if lastValue, !value {
                close(target)
            }
            if !lastValue, value {
                open(target)
            }
            lastValue = value
        }
    }
     public func openCloseBinding<A: AnyObject>(_ target: A, _ open: @escaping (A) -> Void, _ close: @escaping (A) -> Void) -> Void {
        return openCloseBinding(target: target, open: open, close: close)
    }
}
 

extension ObservableProperty where T == Bool {
    public func openCloseBinding(open: @escaping () -> Void, close: @escaping () -> Void) -> Void {
        var lastValue = self.value
        if self.value {
            open()
        }
        var everlasting = self.observableNN.subscribeBy{ (value) in 
            if lastValue, !value {
                close()
            }
            if !lastValue, value {
                open()
            }
            lastValue = value
        }
    }
    public func openCloseBinding(_ open: @escaping () -> Void, _ close: @escaping () -> Void) -> Void {
        return openCloseBinding(open: open, close: close)
    }
}
 

extension ObservableProperty where T == Bool {
     public func once() -> ObservableProperty<Bool> {
        return OnceObservableProperty(self)
    }
}
 

private class OnceObservableProperty: ObservableProperty<Bool> {
    
    public var basedOn: ObservableProperty<Bool>
    
    override public var value: Bool {
        get {
            return basedOn.value
        }
    }
    override public var onChange: Observable<Bool> {
        get {
            return basedOn.onChange.take(1)
        }
    }
    
    public init(basedOn: ObservableProperty<Bool>) {
        self.basedOn = basedOn
        super.init()
    }
    convenience public init(_ basedOn: ObservableProperty<Bool>) {
        self.init(basedOn: basedOn)
    }
}
 

extension ObservableProperty where T == Bool {
     public func closeWhenOff(closeable: Disposable) -> Void {
        var listener: Disposable?  = nil
        listener = self.observableNN.subscribe{ (it) in 
            if !it {
                closeable.dispose()
                listener?.dispose()
            }
        }
    }
     public func closeWhenOff(_ closeable: Disposable) -> Void {
        return closeWhenOff(closeable: closeable)
    }
}
 
public var appInForeground = StandardObservableProperty(false) 
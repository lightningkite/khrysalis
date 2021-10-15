//Stub file made with Butterfly 2 (by Lightning Kite)
import Foundation
import CoreGraphics
import RxSwift

//--- delay(Long, ()->Unit)
public func delay(_ milliseconds: Int64, _ action: @escaping () -> Void) -> Void {
    if milliseconds == 0 {
        action()
    } else {
        DispatchQueue.main.asyncAfter(deadline: .now() + DispatchTimeInterval.milliseconds(Int(milliseconds)), execute: action)
    }
}
public func delay(milliseconds: Int64, action: @escaping () -> Void) -> Void {
    return delay(milliseconds, action)
}

//--- post(()->Unit)
public func post(action: @escaping () -> Void) -> Void {
    DispatchQueue.main.asyncAfter(deadline: .now() + 0.0000001, execute: action)
}

//--- animationFrame
public let animationFrame: PublishSubject<CGFloat> = {
    let temp = PublishSubject<CGFloat>()
    frame()
    return temp
}()

private func frame(){
    let start = Date()
    delay(15){
        let end = Date()
        animationFrame.invokeAll(CGFloat(end.timeIntervalSince(start)))
        frame()
    }
}

public var applicationIsActive: ObservableProperty<Bool> {
    return ApplicationAccess.INSTANCE.foreground
}

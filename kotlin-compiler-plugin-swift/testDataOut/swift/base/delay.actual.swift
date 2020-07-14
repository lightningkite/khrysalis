//Stub file made with Khrysalis 2 (by Lightning Kite)
import Foundation
import CoreGraphics

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
public func post(_ action: @escaping () -> Void) -> Void {
    DispatchQueue.main.asyncAfter(deadline: .now() + 0.01, execute: action)
}

//--- animationFrame
public let animationFrame: StandardEvent<CGFloat> = {
    let temp = StandardEvent<CGFloat>()
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

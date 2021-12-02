//Stub file made with Butterfly 2 (by Lightning Kite)
import Foundation
import UIKit


public extension CGRect {

    init(left: CGFloat, top: CGFloat, right: CGFloat, bottom: CGFloat) {
        self.init(x: left, y: top, width: right - left, height: bottom - top)
    }

    var right: CGFloat {
        get {
            return CGFloat(origin.x + size.width)
        }
        set(value){
            size.width = CGFloat(value) - origin.x
        }
    }
    var bottom: CGFloat {
        get {
            return CGFloat(origin.y + size.height)
        }
        set(value){
            size.height = CGFloat(value) - origin.y
        }
    }
    var left: CGFloat {
        get {
            return CGFloat(origin.x)
        }
        set(value){
            let cg = CGFloat(value)
            size.width -= cg - origin.x
            origin.x = cg
        }
    }
    var top: CGFloat {
        get {
            return CGFloat(origin.y)
        }
        set(value){
            let cg = CGFloat(value)
            size.height -= cg - origin.y
            origin.y = cg
        }
    }
    mutating func inset(_ dx: CGFloat, _ dy: CGFloat) {
        self = self.insetBy(dx: CGFloat(dx), dy: CGFloat(dy))
    }

}

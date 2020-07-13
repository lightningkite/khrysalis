//Stub file made with Khrysalis 2 (by Lightning Kite)
import Foundation
import UIKit


//--- RectF.{
extension CGPoint: Hashable {
    public func hash(into hasher: inout Hasher){
        hasher.combine(x)
        hasher.combine(y)
    }
}

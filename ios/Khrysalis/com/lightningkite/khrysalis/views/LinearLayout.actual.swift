//Stub file made with Khrysalis 2 (by Lightning Kite)
import Foundation
import UIKit


//--- LinearLayout.params(Int, Int, Int, Int, Int, Int, AlignPair, Float)
public extension LinearLayout {
    func params(
        _ sizeX: Int32,
        _ sizeY: Int32 = 0,
        _ marginStart: Int32 = 0,
        _ marginEnd: Int32 = 0,
        _ marginTop: Int32 = 0,
        _ marginBottom: Int32 = 0,
        _ gravity: AlignPair = .center,
        _ weight: Float = 0
    ) -> LinearLayout.LayoutParams {
        return LinearLayout.LayoutParams(
            minimumSize: .zero,
            size: CGSize(width: Int(sizeX), height: Int(sizeY)),
            margin: UIEdgeInsets(
                top: CGFloat(marginTop),
                left: CGFloat(marginStart),
                bottom: CGFloat(marginBottom),
                right: CGFloat(marginEnd)
            ),
            gravity: gravity,
            weight: CGFloat(weight)
        )
    }
    func params(
        sizeX: Int32 = 0,
        sizeY: Int32 = 0,
        marginStart: Int32 = 0,
        marginEnd: Int32 = 0,
        marginTop: Int32 = 0,
        marginBottom: Int32 = 0,
        gravity: AlignPair = .center,
        weight: Float = 0
    ) -> LinearLayout.LayoutParams {
        return params(sizeX, sizeY, marginStart, marginEnd, marginTop, marginBottom, gravity, weight)
    }
}

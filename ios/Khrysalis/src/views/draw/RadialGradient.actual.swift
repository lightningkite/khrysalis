//Stub file made with Khrysalis 2 (by Lightning Kite)
import Foundation
import CoreGraphics
import UIKit


//--- newRadialGradient(CGFloat, CGFloat, CGFloat, List<Int>, List<CGFloat>, Shader.TileMode)
public func newRadialGradient(
    centerX: CGFloat,
    centerY: CGFloat,
    radius: CGFloat,
    colors: List<UIColor>,
    stops: List<GFloat>,
    tile: Shader.TileMode
) -> ShaderValue {
    return { context in
        context.drawRadialGradient(
            CGGradient(
                colorsSpace: CGColorSpaceCreateDeviceRGB(),
                colors: colors.map { $0.cgColor } as CFArray,
                locations: positions.map { CGFloat($0) }
            )!,
            startCenter: CGPoint(x: CGFloat(x0), y: CGFloat(y0)),
            startRadius: 0
            endCenter: CGPoint(x: CGFloat(x0), y: CGFloat(y0)),
            endRadius: radius,
            options: []
        )
    }
}

//Stub file made with Khrysalis 2 (by Lightning Kite)
import Foundation
import CoreGraphics
import UIKit


//--- newRadialGradient(CGFloat, CGFloat, CGFloat, List<Int>, List<CGFloat>, Shader.TileMode)
public func newRadialGradient(
    centerX: CGFloat,
    centerY: CGFloat,
    radius: CGFloat,
    colors: Array<UIColor>,
    stops: Array<CGFloat>,
    tile: Shader.TileMode
) -> ShaderValue {
    return { context in
        context.drawRadialGradient(
            CGGradient(
                colorsSpace: CGColorSpaceCreateDeviceRGB(),
                colors: colors.map { $0.cgColor } as CFArray,
                locations: stops.map { CGFloat($0) }
            )!,
            startCenter: CGPoint(x: CGFloat(centerX), y: CGFloat(centerY)),
            startRadius: 0,
            endCenter: CGPoint(x: CGFloat(centerX), y: CGFloat(centerY)),
            endRadius: radius,
            options: []
        )
    }
}

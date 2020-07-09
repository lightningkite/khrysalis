//Stub file made with Khrysalis 2 (by Lightning Kite)
import Foundation
import CoreGraphics
import UIKit


//--- newLinearGradient(GFloat, GFloat, GFloat, GFloat, List<Int>, List<GFloat>, Shader.TileMode)
public func newLinearGradient(x0: GFloat, y0: GFloat, x1: GFloat, y1: GFloat, colors: Array<UIColor>, positions: Array<GFloat>, tile: Shader.TileMode) -> ShaderValue {
    return newLinearGradient(x0, y0, x1, y1, colors, positions, tile)
}

public func newLinearGradient(_ x0: GFloat, _ y0: GFloat, _ x1: GFloat, _ y1: GFloat, _ colors: Array<UIColor>, _ positions: Array<GFloat>, _ tile: Shader.TileMode) -> ShaderValue {
    return { context in
        context.drawLinearGradient(
            CGGradient(
                colorsSpace: CGColorSpaceCreateDeviceRGB(),
                colors: colors.map { $0.cgColor } as CFArray,
                locations: positions.map { CGFloat($0) }
            )!,
            start: CGPoint(x: CGFloat(x0), y: CGFloat(y0)),
            end: CGPoint(x: CGFloat(x1), y: CGFloat(y1)),
            options: []
        )
    }
}

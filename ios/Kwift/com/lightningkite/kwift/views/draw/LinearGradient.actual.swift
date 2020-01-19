//Stub file made with Kwift 2 (by Lightning Kite)
import Foundation
import CoreGraphics
import UIKit


//--- LinearGradient(Float, Float, Float, Float, List<Int>, List<Float>, Shader.TileMode)
public func LinearGradient(_ x0: Float, _ y0: Float, _ x1: Float, _ y1: Float, _ colors: Array<UIColor>, _ positions: Array<Float>, _ tile: Shader.TileMode) -> ShaderValue {
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















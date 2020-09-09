//Stub file made with Khrysalis 2 (by Lightning Kite)
import Foundation
import CoreGraphics
import UIKit


//--- Path
public typealias Path = CGMutablePath

//--- Path.moveTo(CGFloat, CGFloat)
public extension Path {
    func moveTo(_ x: CGFloat, _ y: CGFloat) -> Void {
        self.move(to: CGPoint(x: CGFloat(x), y: CGFloat(y)))
    }
    func moveTo(x: CGFloat, y: CGFloat) -> Void {
        return moveTo(x, y)
    }
}

//--- Path.lineTo(CGFloat, CGFloat)
public extension Path {
    func lineTo(_ x: CGFloat, _ y: CGFloat) -> Void {
        self.addLine(to: CGPoint(x: CGFloat(x), y: CGFloat(y)))
    }
    func lineTo(x: CGFloat, y: CGFloat) -> Void {
        return lineTo(x, y)
    }
}

//--- Path.quadTo(CGFloat, CGFloat, CGFloat, CGFloat)
public extension Path {
    func quadTo(_ cx: CGFloat, _ cy: CGFloat, _ dx: CGFloat, _ dy: CGFloat) -> Void {
        self.addQuadCurve(to: CGPoint(x: CGFloat(dx), y: CGFloat(dy)), control: CGPoint(x: CGFloat(cx), y: CGFloat(cy)))
    }
    func quadTo(cx: CGFloat, cy: CGFloat, dx: CGFloat, dy: CGFloat) -> Void {
        return quadTo(cx, cy, dx, dy)
    }
}

//--- Path.cubicTo(CGFloat, CGFloat, CGFloat, CGFloat, CGFloat, CGFloat)
public extension Path {
    func cubicTo(_ c1x: CGFloat, _ c1y: CGFloat, _ c2x: CGFloat, _ c2y: CGFloat, _ dx: CGFloat, _ dy: CGFloat) -> Void {
        self.addCurve(to: CGPoint(x: CGFloat(dx), y: CGFloat(dy)), control1: CGPoint(x: CGFloat(c1x), y: CGFloat(c1y)), control2: CGPoint(x: CGFloat(c2x), y: CGFloat(c2y)))
    }
    func cubicTo(c1x: CGFloat, c1y: CGFloat, c2x: CGFloat, c2y: CGFloat, dx: CGFloat, dy: CGFloat) -> Void {
        return cubicTo(c1x, c1y, c2x, c2y, dx, dy)
    }
}

//--- Path.close()
public extension Path {
    func close() -> Void {
        self.closeSubpath()
    }
}

//--- Canvas.drawArc
private extension CGFloat {
    var squared: CGFloat { return self * self }
}
public extension Path {
    func arcTo(radius: CGSize, rotation: CGFloat, largeArcFlag: Bool, sweepFlag: Bool, end: CGPoint) {
        arcTo(radius, rotation, largeArcFlag, sweepFlag, end)
    }
    func arcTo(_ radius: CGSize, _ rotation: CGFloat, _ largeArcFlag: Bool, _ sweepFlag: Bool, _ end: CGPoint) {
        let start = self.currentPoint
        if radius.width == 0 || radius.height == 0 {
            addLine(to: end)
            return
        }
        let rotationRadians = (rotation * CGFloat.pi/180).truncatingRemainder(dividingBy: CGFloat.pi * 2)
        let cosRotation = cos(rotationRadians)
        let sinRotation = sin(rotationRadians)

        // Calculate arc center
        let p1 = CGPoint(
            x: cosRotation * (start.x - end.x) / 2 + sinRotation * (start.y - end.y) / 2,
            y: -sinRotation * (start.x - end.x) / 2 + cosRotation * (start.y - end.y) / 2
        )
        let delta = p1.x.squared / radius.width.squared + p1.y.squared / radius.height.squared
        let transformedRadius = delta <= 1.0 ? radius : CGSize(width: radius.width * sqrt(delta), height: radius.height * sqrt(delta))
        let center = ({ () -> CGPoint in
            let numerator = transformedRadius.width.squared * transformedRadius.height.squared - transformedRadius.width.squared * p1.y.squared - transformedRadius.height.squared * p1.x.squared
            let denom = transformedRadius.width.squared * p1.y.squared + transformedRadius.height.squared * p1.x.squared
            let lhs = denom == 0 ? 0 : (largeArcFlag == sweepFlag ? -1 : 1) * sqrt(max(numerator, 0) / denom)
            let cxp = lhs * transformedRadius.width * p1.y / transformedRadius.height
            let cyp = lhs * -transformedRadius.height * p1.x / transformedRadius.width
            let c = CGPoint(
                x: cosRotation * cxp - sinRotation * cyp + (start.x + end.x) / 2,
                y: sinRotation * cxp + cosRotation * cyp + (start.y + end.y) / 2
            )

            return c
        })()

        // Transform ellipse into unit circle and calculate angles
        var transform = CGAffineTransform(scaleX: 1/transformedRadius.width, y: 1/transformedRadius.height)
        transform = transform.rotated(by: -rotationRadians)
        transform = transform.translatedBy(x: -center.x, y: -center.y)
        let transformedStart = start.applying(transform)
        let transformedEnd = end.applying(transform)
        let startAngle = atan2(transformedStart.y, transformedStart.x)
        let endAngle = atan2(transformedEnd.y, transformedEnd.x)
        var deltaAngle = endAngle - startAngle
        if sweepFlag {
            if deltaAngle < 0 {
                deltaAngle += 2 * .pi
            }
        } else {
            if deltaAngle > 0 {
                deltaAngle -= 2 * .pi
            }
        }

        // Draw
        let reversedTransform = transform.inverted()
        self.addRelativeArc(
            center: .zero,
            radius: 1,
            startAngle: startAngle,
            delta: deltaAngle,
            transform: reversedTransform
        )
    }
}

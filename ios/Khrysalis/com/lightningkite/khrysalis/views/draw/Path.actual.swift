//Stub file made with Khrysalis 2 (by Lightning Kite)
import Foundation
import CoreGraphics
import UIKit


//--- Path
public typealias Path = CGMutablePath

//--- Path.moveTo(GFloat, GFloat)
public extension Path {
    func moveTo(_ x: GFloat, _ y: GFloat) -> Void {
        self.move(to: CGPoint(x: CGFloat(x), y: CGFloat(y)))
    }
    func moveTo(x: GFloat, y: GFloat) -> Void {
        return moveTo(x, y)
    }
}

//--- Path.lineTo(GFloat, GFloat)
public extension Path {
    func lineTo(_ x: GFloat, _ y: GFloat) -> Void {
        self.addLine(to: CGPoint(x: CGFloat(x), y: CGFloat(y)))
    }
    func lineTo(x: GFloat, y: GFloat) -> Void {
        return lineTo(x, y)
    }
}

//--- Path.quadTo(GFloat, GFloat, GFloat, GFloat)
public extension Path {
    func quadTo(_ cx: GFloat, _ cy: GFloat, _ dx: GFloat, _ dy: GFloat) -> Void {
        self.addQuadCurve(to: CGPoint(x: CGFloat(dx), y: CGFloat(dy)), control: CGPoint(x: CGFloat(cx), y: CGFloat(cy)))
    }
    func quadTo(cx: GFloat, cy: GFloat, dx: GFloat, dy: GFloat) -> Void {
        return quadTo(cx, cy, dx, dy)
    }
}

//--- Path.cubicTo(GFloat, GFloat, GFloat, GFloat, GFloat, GFloat)
public extension Path {
    func cubicTo(_ c1x: GFloat, _ c1y: GFloat, _ c2x: GFloat, _ c2y: GFloat, _ dx: GFloat, _ dy: GFloat) -> Void {
        self.addCurve(to: CGPoint(x: CGFloat(dx), y: CGFloat(dy)), control1: CGPoint(x: CGFloat(c1x), y: CGFloat(c1y)), control2: CGPoint(x: CGFloat(c2x), y: CGFloat(c2y)))
    }
    func cubicTo(c1x: GFloat, c1y: GFloat, c2x: GFloat, c2y: GFloat, dx: GFloat, dy: GFloat) -> Void {
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
public extension Path {
    private func sqr(_ value: CGFloat) -> CGFloat {
        return value * value
    }
    private func svgAngle(_ ux: CGFloat, _ uy: CGFloat, _ vx: CGFloat, _ vy: CGFloat) -> CGFloat {
        let dot = ux * vx + uy * vy
        let len = sqrt(sqr(vx-ux) + sqr(vy-uy))
        var angle = acos(max(-1, min(1, dot/len)))
        if (ux*vy - uy*vx) < 0 {
          angle = -angle
        }
        return angle
    }
    func arcTo(radius: CGSize, rotation: CGFloat, largeArcFlag: Bool, sweepFlag: Bool, end: CGPoint) {
        arcTo(radius, rotation, largeArcFlag, sweepFlag, end)
    }
    func arcTo(_ radius: CGSize, _ rotation: CGFloat, _ largeArcFlag: Bool, _ sweepFlag: Bool, _ end: CGPoint) {
        let start = self.currentPoint
        var rx = radius.width
        var ry = radius.height
        var x_rot = rotation
        let large = largeArcFlag
        let sweep = sweepFlag
        let ax = start.x
        let ay = start.y
        let bx = end.x
        let by = end.y
        
        x_rot *= CGFloat.pi/180
        
        rx = abs(rx)
        ry = abs(ry)

        let dx2 = (ax - bx) / 2
        let dy2 = (ay - by) / 2
        let x1p =  cos(x_rot)*dx2 + sin(x_rot)*dy2
        let y1p = -sin(x_rot)*dx2 + cos(x_rot)*dy2

        var rxs = rx * rx
        var rys = ry * ry
        let x1ps = x1p * x1p
        let y1ps = y1p * y1p
        // check if the radius is too small `pq < 0`, when `dq > rxs * rys` (see below)
        // cr is the ratio (dq : rxs * rys)
        let cr = x1ps/rxs + y1ps/rys
        var s: CGFloat = 1
        if (cr > 1) {
          //scale up rX,rY equally so cr == 1
          s = sqrt(cr)
          rx = s * rx
          ry = s * ry
          rxs = rx * rx
          rys = ry * ry
        }
        let dq = (rxs * y1ps + rys * x1ps)
        let pq = (rxs*rys - dq) / dq
        var q = sqrt( max(0,pq) ) //use Max to account for GFloat precision
        if large == sweep {
          q = -q
        }
        let cxp = q * rx * y1p / ry
        let cyp = -q * ry * x1p / rx

        //(F.6.5.3)
        let cx = cos(x_rot)*cxp - sin(x_rot)*cyp + (ax + bx)/2
        let cy = sin(x_rot)*cxp + cos(x_rot)*cyp + (ay + by)/2

        //(F.6.5.5)
        var theta = svgAngle( 1,0, (x1p-cxp) / rx, (y1p - cyp)/ry )
        //(F.6.5.6)
        var delta = svgAngle(
          (x1p - cxp)/rx, (y1p - cyp)/ry,
          (-x1p - cxp)/rx, (-y1p-cyp)/ry)
        delta = delta.truncatingRemainder(dividingBy: CGFloat.pi * 2)
        if (!sweep) {
          delta -= 2 * CGFloat.pi
        }

        self.addRelativeArc(
            center: CGPoint(x: cx, y: cy),
            radius: max(rx, ry),
            startAngle: theta,
            delta: delta,
            transform: CGAffineTransform(rotationAngle: x_rot)
        )
    }
}

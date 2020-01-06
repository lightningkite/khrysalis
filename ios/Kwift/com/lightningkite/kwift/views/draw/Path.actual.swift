//Stub file made with Kwift 2 (by Lightning Kite)
import Foundation


//--- Path
public typealias Path = CGMutablePath

//--- Path.moveTo(Float, Float)
public extension Path {
    func moveTo(_ x: Float, _ y: Float) -> Void {
        self.move(to: CGPoint(x: CGFloat(x), y: CGFloat(y)))
    }
    func moveTo(x: Float, y: Float) -> Void {
        return moveTo(x, y)
    }
}

//--- Path.lineTo(Float, Float)
public extension Path {
    func lineTo(_ x: Float, _ y: Float) -> Void {
        self.addLine(to: CGPoint(x: CGFloat(x), y: CGFloat(y)))
    }
    func lineTo(x: Float, y: Float) -> Void {
        return lineTo(x, y)
    }
}

//--- Path.quadTo(Float, Float, Float, Float)
public extension Path {
    func quadTo(_ cx: Float, _ cy: Float, _ dx: Float, _ dy: Float) -> Void {
        self.addQuadCurve(to: CGPoint(x: CGFloat(dx), y: CGFloat(dy)), control: CGPoint(x: CGFloat(cx), y: CGFloat(cy)))
    }
    func quadTo(cx: Float, cy: Float, dx: Float, dy: Float) -> Void {
        return quadTo(cx, cy, dx, dy)
    }
}

//--- Path.cubicTo(Float, Float, Float, Float, Float, Float)
public extension Path {
    func cubicTo(_ c1x: Float, _ c1y: Float, _ c2x: Float, _ c2y: Float, _ dx: Float, _ dy: Float) -> Void {
        self.addCurve(to: CGPoint(x: CGFloat(dx), y: CGFloat(dy)), control1: CGPoint(x: CGFloat(c1x), y: CGFloat(c1y)), control2: CGPoint(x: CGFloat(c2x), y: CGFloat(c2y)))
    }
    func cubicTo(c1x: Float, c1y: Float, c2x: Float, c2y: Float, dx: Float, dy: Float) -> Void {
        return cubicTo(c1x, c1y, c2x, c2y, dx, dy)
    }
}

//--- Path.close()
public extension Path {
    func close() -> Void {
        self.closeSubpath()
    }
}


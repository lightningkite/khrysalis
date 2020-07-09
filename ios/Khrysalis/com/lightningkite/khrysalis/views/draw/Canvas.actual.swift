//Stub file made with Khrysalis 2 (by Lightning Kite)
import Foundation
import CoreGraphics
import UIKit


//--- Canvas
public typealias Canvas = CGContext
public extension Canvas {
    func completePath(_ path: CGPath, _ paint: Paint) {
        if let shader = paint.shader {
            UIBezierPath(cgPath: path).addClip()
            shader(self)
            resetClip()
        } else {
            setAlpha(CGFloat(paint.alpha) / 255)
            setLineWidth(CGFloat(paint.strokeWidth))
            self.addPath(path)
            switch paint.style {
            case .FILL:
                setFillColor(paint.color.cgColor)
                fillPath()
            case .FILL_AND_STROKE:
                setFillColor(paint.color.cgColor)
                setStrokeColor(paint.color.cgColor)
                fillPath()
                strokePath()
            case .STROKE:
                setStrokeColor(paint.color.cgColor)
                strokePath()
            }
        }
    }
}

//--- Canvas.clipRect(GFloat, GFloat, GFloat, GFloat)
//--- Canvas.clipRect(RectF)
public extension Canvas {
    func clipRect(_ left: GFloat, _ top: GFloat, _ right: GFloat, _ bottom: GFloat){
        clip(to: CGRect(
            x: CGFloat(left),
            y: CGFloat(top),
            width: CGFloat(right-left),
            height: CGFloat(bottom-top)
        ))
    }
    func clipRect(_ rect: RectF){
        clip(to: rect)
    }
}

//--- Canvas.drawCircle(GFloat, GFloat, GFloat, Paint)
public extension Canvas {
    func drawCircle(_ cx: GFloat, _ cy: GFloat, _ radius: GFloat, _ paint: Paint) {
        let path = Path()
        path.addArc(center: CGPoint(x: CGFloat(cx), y: CGFloat(cy)), radius: CGFloat(radius), startAngle: 0, endAngle: CGFloat.pi * 2, clockwise: true)
        path.closeSubpath()
        self.completePath(path, paint)
    }
}

//--- Canvas.drawRect(GFloat, GFloat, GFloat, GFloat, Paint)
//--- Canvas.drawRect(RectF, Paint)
public extension Canvas {
    func drawRect(_ left: GFloat, _ top: GFloat, _ right: GFloat, _ bottom: GFloat, _ paint: Paint) {
        self.completePath(
            CGPath(
                rect: CGRect(
                    x: CGFloat(left),
                    y: CGFloat(top),
                    width: CGFloat(right-left),
                    height: CGFloat(bottom-top)
                ),
                transform: nil
            ),
            paint
        )
    }
    func drawRect(_ rect: RectF, _ paint: Paint) {
        self.completePath(
            CGPath(
                rect: rect,
                transform: nil
            ),
            paint
        )
    }
}

//--- Canvas.drawOval(GFloat, GFloat, GFloat, GFloat, Paint)
//--- Canvas.drawOval(RectF, Paint)
public extension Canvas {
    func drawOval(_ left: GFloat, _ top: GFloat, _ right: GFloat, _ bottom: GFloat, _ paint: Paint) {
        self.completePath(
            CGPath(
                ellipseIn: CGRect(
                    x: CGFloat(left),
                    y: CGFloat(top),
                    width: CGFloat(right-left),
                    height: CGFloat(bottom-top)
                ),
                transform: nil
            ),
            paint
        )
    }
    func drawOval(_ rect: RectF, _ paint: Paint) {
        self.completePath(
            CGPath(
                ellipseIn: rect,
                transform: nil
            ),
            paint
        )
    }
}

//--- Canvas.drawRoundRect(GFloat, GFloat, GFloat, GFloat, GFloat, GFloat, Paint)
//--- Canvas.drawRoundRect(RectF, GFloat, GFloat, Paint)
public extension Canvas {
    func drawRoundRect(_ left: GFloat, _ top: GFloat, _ right: GFloat, _ bottom: GFloat, _ rx: GFloat, _ ry: GFloat, _ paint: Paint) {
        self.completePath(
            CGPath(
                roundedRect: CGRect(x: CGFloat(left), y: CGFloat(top), width: CGFloat(right-left), height: CGFloat(bottom-top)),
                cornerWidth: CGFloat(rx),
                cornerHeight: CGFloat(ry),
                transform: nil
            ),
            paint
        )
    }
    func drawRoundRect(_ rect: RectF, _ rx: GFloat, _ ry: GFloat, _ paint: Paint) {
        self.completePath(
            CGPath(
                roundedRect: rect,
                cornerWidth: CGFloat(rx),
                cornerHeight: CGFloat(ry),
                transform: nil
            ),
            paint
        )
    }
}

//--- Canvas.drawLine(GFloat, GFloat, GFloat, GFloat, Paint)
public extension Canvas {
    func drawLine(_ x1: GFloat, _ y1: GFloat, _ x2: GFloat, _ y2: GFloat, _ paint: Paint) {
        let path = Path()
        path.moveTo(x1, y1)
        path.lineTo(x2, y2)
        self.completePath(
            path,
            paint
        )
    }
}

//--- Canvas.drawPath(Path, Paint)
public extension Canvas {
    func drawPath(_ path: Path, _ paint: Paint) {
        self.completePath(path, paint)
    }
}

//--- Canvas.save()
//--- Canvas.restore()
public extension Canvas {
    func save(){
        self.saveGState()
    }
    
    func restore(){
        self.restoreGState()
    }
}

//--- Canvas.translate(GFloat, GFloat)
public extension Canvas {
    func translate(_ dx: GFloat, _ dy: GFloat) {
        self.translateBy(x: CGFloat(dx), y: CGFloat(dy))
    }
}

//--- Canvas.scale(GFloat, GFloat)
public extension Canvas {
    func scale(_ scaleX: GFloat, _ scaleY: GFloat) {
        self.scaleBy(x: CGFloat(scaleX), y: CGFloat(scaleY))
    }
}

//--- Canvas.rotate(GFloat)
public extension Canvas {
    func rotate(_ degrees: GFloat) {
        self.rotate(by: CGFloat(degrees) * CGFloat.pi / 180)
    }
}

//--- Canvas.drawTextCentered(String, GFloat, GFloat, Paint)
public extension Canvas {
    func drawTextCentered(_ text: String, _ centerX: GFloat, _ centerY: GFloat, _ paint: Paint) {
        drawText(text, centerX, centerY, .center, paint)
    }
}

//--- Canvas.drawText(String, GFloat, GFloat, AlignPair, Paint)
public extension Canvas {
    func drawText(_ text: String, _ x: GFloat, _ y: GFloat, _ gravity: AlignPair, _ paint: Paint) {
        let sizeTaken = CGFloat(paint.measureText(text))
        var dx = CGFloat(x)
        var dy = CGFloat(y)
        switch gravity.horizontal {
        case .start:
            break
        case .fill, .center:
            dx = dx - sizeTaken / 2
        case .end:
            dx = dx - sizeTaken
        }
        switch gravity.vertical {
        case .start:
            break
        case .fill, .center:
            dy = dy - CGFloat(paint.textHeight) / 2
        case .end:
            dy = dy - CGFloat(paint.textHeight)
        }
        text.draw(at: CGPoint(x: dx, y: dy), withAttributes: paint.attributes)
    }
    func drawText(text: String, x: GFloat, y: GFloat, gravity: AlignPair, paint: Paint) {
        drawText(text, x, y, gravity, paint)
    }
}

//--- tempRect

//--- Canvas.drawBitmap(Bitmap, GFloat, GFloat, GFloat, GFloat)
public extension Canvas {
    func drawBitmap(_ bitmap: UIImage, _ left: GFloat, _ top: GFloat) {
        if let cg = bitmap.cgImage {
            self.draw(cg, in: CGRect(x: CGFloat(left), y: CGFloat(top), width: CGFloat(cg.width), height: CGFloat(cg.height)))
        }
    }
    
    func drawBitmap(_ bitmap: UIImage, _ left: GFloat, _ top: GFloat, _ right: GFloat, _ bottom: GFloat) {
        if let cg = bitmap.cgImage {
            self.draw(cg, in: CGRect(x: CGFloat(left), y: CGFloat(top), width: CGFloat(right-left), height: CGFloat(bottom-top)))
        }
    }
}

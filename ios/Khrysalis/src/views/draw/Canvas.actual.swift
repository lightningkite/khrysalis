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

//--- Canvas.clipRect(CGFloat, CGFloat, CGFloat, CGFloat)
//--- Canvas.clipRect(RectF)
public extension Canvas {
    func clipRect(_ left: CGFloat, _ top: CGFloat, _ right: CGFloat, _ bottom: CGFloat){
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

//--- Canvas.drawCircle(CGFloat, CGFloat, CGFloat, Paint)
public extension Canvas {
    func drawCircle(_ cx: CGFloat, _ cy: CGFloat, _ radius: CGFloat, _ paint: Paint) {
        let path = Path()
        path.addArc(center: CGPoint(x: CGFloat(cx), y: CGFloat(cy)), radius: CGFloat(radius), startAngle: 0, endAngle: CGFloat.pi * 2, clockwise: true)
        path.closeSubpath()
        self.completePath(path, paint)
    }
}

//--- Canvas.drawRect(CGFloat, CGFloat, CGFloat, CGFloat, Paint)
//--- Canvas.drawRect(RectF, Paint)
public extension Canvas {
    func drawRect(_ left: CGFloat, _ top: CGFloat, _ right: CGFloat, _ bottom: CGFloat, _ paint: Paint) {
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

//--- Canvas.drawOval(CGFloat, CGFloat, CGFloat, CGFloat, Paint)
//--- Canvas.drawOval(RectF, Paint)
public extension Canvas {
    func drawOval(_ left: CGFloat, _ top: CGFloat, _ right: CGFloat, _ bottom: CGFloat, _ paint: Paint) {
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

//--- Canvas.drawRoundRect(CGFloat, CGFloat, CGFloat, CGFloat, CGFloat, CGFloat, Paint)
//--- Canvas.drawRoundRect(RectF, CGFloat, CGFloat, Paint)
public extension Canvas {
    func drawRoundRect(_ left: CGFloat, _ top: CGFloat, _ right: CGFloat, _ bottom: CGFloat, _ rx: CGFloat, _ ry: CGFloat, _ paint: Paint) {
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
    func drawRoundRect(_ rect: RectF, _ rx: CGFloat, _ ry: CGFloat, _ paint: Paint) {
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

//--- Canvas.drawLine(CGFloat, CGFloat, CGFloat, CGFloat, Paint)
public extension Canvas {
    func drawLine(_ x1: CGFloat, _ y1: CGFloat, _ x2: CGFloat, _ y2: CGFloat, _ paint: Paint) {
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

//--- Canvas.translate(CGFloat, CGFloat)
public extension Canvas {
    func translate(_ dx: CGFloat, _ dy: CGFloat) {
        self.translateBy(x: CGFloat(dx), y: CGFloat(dy))
    }
}

//--- Canvas.scale(CGFloat, CGFloat)
public extension Canvas {
    func scale(_ scaleX: CGFloat, _ scaleY: CGFloat) {
        self.scaleBy(x: CGFloat(scaleX), y: CGFloat(scaleY))
    }
}

//--- Canvas.rotate(CGFloat)
public extension Canvas {
    func rotate(_ degrees: CGFloat) {
        self.rotate(by: CGFloat(degrees) * CGFloat.pi / 180)
    }
}

//--- Canvas.drawTextCentered(String, CGFloat, CGFloat, Paint)
public extension Canvas {
    func drawTextCentered(text: String,  centerX: CGFloat,  centerY: CGFloat,  paint: Paint) {
        drawText(text, centerX, centerY, .center, paint)
    }
}

//--- Canvas.drawText(String, CGFloat, CGFloat, AlignPair, Paint)
public extension Canvas {
    func drawText(_ text: String, _ x: CGFloat, _ y: CGFloat, _ gravity: AlignPair, _ paint: Paint) {
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
    func drawText(text: String, x: CGFloat, y: CGFloat, gravity: AlignPair, paint: Paint) {
        drawText(text, x, y, gravity, paint)
    }
}

//--- tempRect

//--- Canvas.drawBitmap(Bitmap, CGFloat, CGFloat, CGFloat, CGFloat)
public extension Canvas {

    //We draw this upside-down to compensate for the reversed coordinate system

    func drawBitmap(_ bitmap: UIImage, _ left: CGFloat, _ top: CGFloat) {
        let bounds = CGRect(x: left, y: top, width: bitmap.size.width, height: bitmap.size.height)
        bitmap.draw(in: bounds)
    }

    func drawBitmap(bitmap: UIImage, left: CGFloat, top: CGFloat, right: CGFloat, bottom: CGFloat) {
        let bounds = CGRect(x: left, y: top, width: right-left, height: bottom-top)
        bitmap.draw(in: bounds)
    }
}

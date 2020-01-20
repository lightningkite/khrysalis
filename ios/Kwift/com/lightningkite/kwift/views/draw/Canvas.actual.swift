//Stub file made with Kwift 2 (by Lightning Kite)
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

//--- Canvas.clipRect(Float, Float, Float, Float)
//--- Canvas.clipRect(RectF)
public extension Canvas {
    func clipRect(_ left: Float, _ top: Float, _ right: Float, _ bottom: Float){
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

//--- Canvas.drawCircle(Float, Float, Float, Paint)
public extension Canvas {
    func drawCircle(_ cx: Float, _ cy: Float, _ radius: Float, _ paint: Paint) {
        let path = Path()
        path.addArc(center: CGPoint(x: CGFloat(cx), y: CGFloat(cy)), radius: CGFloat(radius), startAngle: 0, endAngle: CGFloat.pi * 2, clockwise: true)
        path.closeSubpath()
        self.completePath(path, paint)
    }
}

//--- Canvas.drawRect(Float, Float, Float, Float, Paint)
//--- Canvas.drawRect(RectF, Paint)
public extension Canvas {
    func drawRect(_ left: Float, _ top: Float, _ right: Float, _ bottom: Float, _ paint: Paint) {
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

//--- Canvas.drawOval(Float, Float, Float, Float, Paint)
//--- Canvas.drawOval(RectF, Paint)
public extension Canvas {
    func drawOval(_ left: Float, _ top: Float, _ right: Float, _ bottom: Float, _ paint: Paint) {
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

//--- Canvas.drawRoundRect(Float, Float, Float, Float, Float, Float, Paint)
//--- Canvas.drawRoundRect(RectF, Float, Float, Paint)
public extension Canvas {
    func drawRoundRect(_ left: Float, _ top: Float, _ right: Float, _ bottom: Float, _ rx: Float, _ ry: Float, _ paint: Paint) {
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
    func drawRoundRect(_ rect: RectF, _ rx: Float, _ ry: Float, _ paint: Paint) {
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

//--- Canvas.drawLine(Float, Float, Float, Float, Paint)
public extension Canvas {
    func drawLine(_ x1: Float, _ y1: Float, _ x2: Float, _ y2: Float, _ paint: Paint) {
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

//--- Canvas.translate(Float, Float)
public extension Canvas {
    func translate(_ dx: Float, _ dy: Float) {
        self.translateBy(x: CGFloat(dx), y: CGFloat(dy))
    }
}

//--- Canvas.scale(Float, Float)
public extension Canvas {
    func scale(_ scaleX: Float, _ scaleY: Float) {
        self.scaleBy(x: CGFloat(scaleX), y: CGFloat(scaleY))
    }
}

//--- Canvas.rotate(Float)
public extension Canvas {
    func rotate(_ degrees: Float) {
        self.rotate(by: CGFloat(degrees) * CGFloat.pi / 180)
    }
}

//--- Canvas.drawTextCentered(String, Float, Float, Paint)
public extension Canvas {
    func drawTextCentered(_ text: String, _ centerX: Float, _ centerY: Float, _ paint: Paint) {
        drawText(text, centerX, centerY, .center, paint)
    }
}

//--- Canvas.drawText(String, Float, Float, AlignPair, Paint)
public extension Canvas {
    func drawText(_ text: String, _ x: Float, _ y: Float, _ gravity: AlignPair, _ paint: Paint) {
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
    func drawText(text: String, x: Float, y: Float, gravity: AlignPair, paint: Paint) {
        drawText(text, x, y, gravity, paint)
    }
}

//--- tempRect

//--- Canvas.drawBitmap(Bitmap, Float, Float, Float, Float)
public extension Canvas {
    func drawBitmap(_ bitmap: UIImage, _ left: Float, _ top: Float) {
        if let cg = bitmap.cgImage {
            self.draw(cg, in: CGRect(x: CGFloat(left), y: CGFloat(top), width: CGFloat(cg.width), height: CGFloat(cg.height)))
        }
    }
    
    func drawBitmap(_ bitmap: UIImage, _ left: Float, _ top: Float, _ right: Float, _ bottom: Float) {
        if let cg = bitmap.cgImage {
            self.draw(cg, in: CGRect(x: CGFloat(left), y: CGFloat(top), width: CGFloat(right-left), height: CGFloat(bottom-top)))
        }
    }
}
















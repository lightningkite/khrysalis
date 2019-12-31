//
//  Canvas.swift
//  Alamofire
//
//  Created by Joseph Ivie on 12/19/19.
//

import CoreGraphics

public typealias Canvas = CGContext
public typealias Path = CGMutablePath
public typealias RectF = CGRect
public typealias PointF = CGPoint

public extension RectF {
    init(){
        self = .zero
    }
    var right: Float {
        get {
            return Float(origin.x + size.width)
        }
        set(value){
            size.width = CGFloat(value) - origin.x
        }
    }
    var bottom: Float {
        get {
            return Float(origin.y + size.height)
        }
        set(value){
            size.height = CGFloat(value) - origin.y
        }
    }
    var left: Float {
        get {
            return Float(origin.x)
        }
        set(value){
            let cg = CGFloat(value)
            size.width -= cg - origin.x
            origin.x = cg
        }
    }
    var top: Float {
        get {
            return Float(origin.y)
        }
        set(value){
            let cg = CGFloat(value)
            size.height -= cg - origin.y
            origin.y = cg
        }
    }
    mutating func set(_ left: Float, _ top: Float, _ right: Float, _ bottom: Float) {
        origin.x = CGFloat(left)
        origin.y = CGFloat(top)
        size.width = CGFloat(right - left)
        size.height = CGFloat(bottom - top)
    }
    mutating func set(_ rect: RectF) {
        origin.x = rect.origin.x
        origin.y = rect.origin.y
        size.width = rect.size.width
        size.height = rect.size.height
    }
    func centerX() -> Float {
        return Float(midX)
    }
    func centerY() -> Float {
        return Float(midY)
    }
    func width() -> Float {
        return Float(size.width)
    }
    func height() -> Float {
        return Float(size.height)
    }
    mutating func inset(_ dx: Float, _ dy: Float) {
        self = self.insetBy(dx: CGFloat(dx), dy: CGFloat(dy))
    }
}

public struct Paint {
    public var flags: Int32 = 0
    public var color: UIColor = .black
    public var strokeWidth: Float = 1
    public var style: Style = .FILL
    public var textSize: Float = 12
    public var shader: ((CGContext)->Void)? = nil
    public var isAntiAlias: Bool = false
    public var isFakeBoldText: Bool = false
//    public var typeface: UIFont
    
    public enum Style { case FILL, STROKE, FILL_AND_STROKE }
    
    var attributes: Dictionary<NSAttributedString.Key, Any> {
        return [
            .font: UIFont.get(size: CGFloat(textSize), style: isFakeBoldText ? ["bold"] : []),
            .foregroundColor: color
        ]
    }
    public func measureText(_ text: String) -> Float {
        return Float(NSString(string: text).size(withAttributes: attributes).width)
    }
    public var textHeight: Float {
        let font = UIFont.get(size: CGFloat(textSize), style: [])
        return Float(font.descender + font.ascender)
    }
    
    public init(){
        
    }
}

public enum Shader {
    public enum TileMode {
        case CLAMP, REPEAT, MIRROR
    }
}

public func LinearGradient(_ x0: Float, _ y0: Float, _ x1: Float, _ y1: Float, _ colors: Array<UIColor>, _ positions: Array<Float>, _ tile: Shader.TileMode) -> (CGContext)->Void {
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

public extension Canvas {
    func completePath(_ path: CGPath, _ paint: Paint) {
        if let shader = paint.shader {
            UIBezierPath(cgPath: path).addClip()
            shader(self)
            resetClip()
        } else {
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
    
    func drawCircle(_ cx: Float, _ cy: Float, _ radius: Float, _ paint: Paint) {
        let path = Path()
        path.addArc(center: CGPoint(x: CGFloat(cx), y: CGFloat(cy)), radius: CGFloat(radius), startAngle: 0, endAngle: CGFloat.pi * 2, clockwise: true)
        path.closeSubpath()
        self.completePath(path, paint)
    }
    
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
    
    func drawRect(_ rect: RectF, _ paint: Paint) {
        self.completePath(
            CGPath(
                rect: rect,
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
    
//    func drawColor(_ color: UIColor) {
//        self.clear()
//    }
    
    func drawPath(_ path: Path, _ paint: Paint) {
        self.completePath(path, paint)
    }
    
    func drawTextCentered(_ text: String, _ centerX: Float, _ centerY: Float, _ paint: Paint) {
        let sizeTaken = text.size(withAttributes: paint.attributes)
        text.draw(at: CGPoint(x: CGFloat(centerX) - sizeTaken.width / 2, y: CGFloat(centerY) - sizeTaken.height / 2), withAttributes: paint.attributes)
    }
    
    func drawText(_ text: String, _ x: Float, _ y: Float, _ gravity: AlignPair, _ paint: Paint) {
        let sizeTaken = text.size(withAttributes: paint.attributes)
        var dx = CGFloat(x)
        var dy = CGFloat(y)
        switch gravity.horizontal {
        case .start:
            break
        case .fill, .center:
            dx = dx - sizeTaken.width / 2
        case .end:
            dx = dx - sizeTaken.width
        }
        switch gravity.vertical {
        case .start:
            break
        case .fill, .center:
            dy = dy - sizeTaken.height / 2
        case .end:
            dy = dy - sizeTaken.height
        }
        text.draw(at: CGPoint(x: dx, y: dy), withAttributes: paint.attributes)
    }
    func drawText(text: String, x: Float, y: Float, gravity: AlignPair, paint: Paint) {
        drawText(text, x, y, gravity, paint)
    }
    
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
    
    func save(){
        self.saveGState()
    }
    
    func restore(){
        self.restoreGState()
    }
    
    func translate(_ dx: Float, _ dy: Float) {
        self.translateBy(x: CGFloat(dx), y: CGFloat(dy))
    }
    
    func scale(_ scaleX: Float, _ scaleY: Float) {
        self.scaleBy(x: CGFloat(scaleX), y: CGFloat(scaleY))
    }
    
    func rotate(_ degrees: Float) {
        self.rotate(by: CGFloat(degrees) * CGFloat.pi / 180)
    }
}

public extension Path {
    func moveTo(_ x: Float, _ y: Float) {
        self.move(to: CGPoint(x: CGFloat(x), y: CGFloat(y)))
    }
    func lineTo(_ x: Float, _ y: Float) {
        self.addLine(to: CGPoint(x: CGFloat(x), y: CGFloat(y)))
    }
    func quadTo(_ cx: Float, _ cy: Float, _ dx: Float, _ dy: Float) {
        self.addQuadCurve(to: CGPoint(x: CGFloat(dx), y: CGFloat(dy)), control: CGPoint(x: CGFloat(cx), y: CGFloat(cy)))
    }
    func cubicTo(_ c1x: Float, _ c1y: Float, _ c2x: Float, _ c2y: Float, _ dx: Float, _ dy: Float) {
        self.addCurve(to: CGPoint(x: CGFloat(dx), y: CGFloat(dy)), control1: CGPoint(x: CGFloat(c1x), y: CGFloat(c1y)), control2: CGPoint(x: CGFloat(c2x), y: CGFloat(c2y)))
    }
    func close(){
        self.closeSubpath()
    }
}

public extension Int32 {
    func asColor() -> UIColor {
        return UIColor(argb: Int(self))
    }
}

public extension Int64 {
    func asColor() -> UIColor {
        return UIColor(argb: Int(self))
    }
}

public extension Int {
    func asColor() -> UIColor {
        return UIColor(argb: Int(self))
    }
}

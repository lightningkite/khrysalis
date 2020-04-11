//Automatically created by Khrysalis
import UIKit
import Khrysalis

extension ResourcesDrawables {

static func zComplexDrawable(_ view: UIView? = nil) -> CALayer {
    let layer = CALayer()
    layer.addSublayer({
        let sublayer = zComplexDrawablePart1(view)
        layer.bounds.size = layer.bounds.size.expand(sublayer.bounds.size)
        layer.onResize.startWith(layer.bounds).addWeak(sublayer) { (sublayer, bounds) in 
             var subBounds = bounds 
             subBounds.origin.x += ResourcesDimensions.speechBubbleCorners
             subBounds.size.width -= ResourcesDimensions.speechBubbleCorners
             subBounds.size.width -= ResourcesDimensions.speechBubbleCornersPlusTail
             sublayer.frame = subBounds 
        }
        return sublayer
    }())
    layer.addSublayer({
        let sublayer = zComplexDrawablePart2(view)
        sublayer.frame.size.width = ResourcesDimensions.speechBubbleCorners
        layer.bounds.size = layer.bounds.size.expand(sublayer.bounds.size)
        layer.onResize.startWith(layer.bounds).addWeak(sublayer) { (sublayer, bounds) in 
             var subBounds = bounds 
             subBounds.origin.y += ResourcesDimensions.speechBubbleCorners
             subBounds.size.height -= ResourcesDimensions.speechBubbleCorners
             sublayer.frame = subBounds 
        }
        return sublayer
    }())
    layer.addSublayer({
        let sublayer = zComplexDrawablePart3(view)
        sublayer.frame.size.width = ResourcesDimensions.speechBubbleCorners
        layer.bounds.size = layer.bounds.size.expand(sublayer.bounds.size)
        layer.onResize.startWith(layer.bounds).addWeak(sublayer) { (sublayer, bounds) in 
             var subBounds = bounds 
             subBounds.origin.y += ResourcesDimensions.speechBubbleCorners
             subBounds.size.height -= ResourcesDimensions.speechBubbleCorners
             subBounds.size.width -= ResourcesDimensions.speechBubbleSpacing
             sublayer.frame = subBounds 
        }
        return sublayer
    }())
    layer.addSublayer({
        let sublayer = zComplexDrawablePart4(view)
        sublayer.frame.size.width = ResourcesDimensions.speechBubbleCorners
        sublayer.frame.size.height = ResourcesDimensions.speechBubbleCorners
        layer.bounds.size = layer.bounds.size.expand(sublayer.bounds.size)
        layer.onResize.startWith(layer.bounds).addWeak(sublayer) { (sublayer, bounds) in 
             var subBounds = bounds 
             sublayer.frame = subBounds 
        }
        return sublayer
    }())
    layer.addSublayer({
        let sublayer = zComplexDrawablePart5(view)
        sublayer.frame.size.width = ResourcesDimensions.speechBubbleCorners
        sublayer.frame.size.height = ResourcesDimensions.speechBubbleCorners
        layer.bounds.size = layer.bounds.size.expand(sublayer.bounds.size)
        layer.onResize.startWith(layer.bounds).addWeak(sublayer) { (sublayer, bounds) in 
             var subBounds = bounds 
             subBounds.size.width -= ResourcesDimensions.speechBubbleSpacing
             sublayer.frame = subBounds 
        }
        return sublayer
    }())
    layer.addSublayer({
        let sublayer = zComplexDrawablePart6(view)
        sublayer.frame.size.width = ResourcesDimensions.speechBubbleCorners
        sublayer.frame.size.height = ResourcesDimensions.speechBubbleCorners
        layer.bounds.size = layer.bounds.size.expand(sublayer.bounds.size)
        layer.onResize.startWith(layer.bounds).addWeak(sublayer) { (sublayer, bounds) in 
             var subBounds = bounds 
             sublayer.frame = subBounds 
        }
        return sublayer
    }())
    layer.addSublayer({
        let sublayer = zComplexDrawablePart7(view)
        sublayer.frame.size.width = ResourcesDimensions.speechBubbleCornersPlusTail
        sublayer.frame.size.height = ResourcesDimensions.speechBubbleCornersPlusTail
        layer.bounds.size = layer.bounds.size.expand(sublayer.bounds.size)
        layer.onResize.startWith(layer.bounds).addWeak(sublayer) { (sublayer, bounds) in 
             var subBounds = bounds 
             sublayer.frame = subBounds 
        }
        return sublayer
    }())
    return layer
}
static func zComplexDrawablePart1(_ view: UIView? = nil) -> CALayer {
    let layer = CALayer()
    layer.backgroundColor = ResourcesColors.speechBubbleSelf.cgColor
    layer.bounds.size = CGSize(width: 100, height: 100)
    return layer
}
static func zComplexDrawablePart2(_ view: UIView? = nil) -> CALayer {
    let layer = CALayer()
    layer.backgroundColor = ResourcesColors.speechBubbleSelf.cgColor
    layer.bounds.size = CGSize(width: 100, height: 100)
    return layer
}
static func zComplexDrawablePart3(_ view: UIView? = nil) -> CALayer {
    let layer = CALayer()
    layer.backgroundColor = ResourcesColors.speechBubbleSelf.cgColor
    layer.bounds.size = CGSize(width: 100, height: 100)
    return layer
}
static func zComplexDrawablePart4(_ view: UIView? = nil) -> CALayer {
    let scaleX: CGFloat = CGFloat(ResourcesDimensions.speechBubbleCorners) / 10.0
    let scaleY: CGFloat = CGFloat(ResourcesDimensions.speechBubbleCorners) / 10.0
    let layer = CALayer()
    layer.addSublayer({
        let sublayer = CAShapeLayer()
        let path = CGMutablePath()
        //M 0.0, 0.0
        path.move(to: CGPoint(x: 0.0 * scaleX, y: 0.0 * scaleY))
        //A 10.0, 10.0, 0.0, 0.0, 0.0, 10.0, 10.0
        path.arcTo(radius: CGSize(width: 10.0 * scaleX, height: 10.0 * scaleY), rotation: 0.0, largeArcFlag: false, sweepFlag: false, end: CGPoint(x: 10.0 * scaleX, y: 10.0 * scaleY))
        //L 10.0, 0.0
        path.addLine(to: CGPoint(x: 10.0 * scaleX, y: 0.0 * scaleY))
        //Z 
        sublayer.path = path
        sublayer.fillColor = ResourcesColors.speechBubbleSelf.cgColor
        return sublayer
    }())
    layer.bounds.size = CGSize(width: 0.0, height: 0.0)
    layer.scaleOverResize = true
    return layer
}
static func zComplexDrawablePart5(_ view: UIView? = nil) -> CALayer {
    let scaleX: CGFloat = CGFloat(ResourcesDimensions.speechBubbleCorners) / 10.0
    let scaleY: CGFloat = CGFloat(ResourcesDimensions.speechBubbleCorners) / 10.0
    let layer = CALayer()
    layer.addSublayer({
        let sublayer = CAShapeLayer()
        let path = CGMutablePath()
        //M 10.0, 10.0
        path.move(to: CGPoint(x: 10.0 * scaleX, y: 10.0 * scaleY))
        //A 10.0, 10.0, 0.0, 0.0, 0.0, 0.0, 0.0
        path.arcTo(radius: CGSize(width: 10.0 * scaleX, height: 10.0 * scaleY), rotation: 0.0, largeArcFlag: false, sweepFlag: false, end: CGPoint(x: 0.0 * scaleX, y: 0.0 * scaleY))
        //L 0.0, 10.0
        path.addLine(to: CGPoint(x: 0.0 * scaleX, y: 10.0 * scaleY))
        //Z 
        sublayer.path = path
        sublayer.fillColor = ResourcesColors.speechBubbleSelf.cgColor
        return sublayer
    }())
    layer.bounds.size = CGSize(width: 0.0, height: 0.0)
    layer.scaleOverResize = true
    return layer
}
static func zComplexDrawablePart6(_ view: UIView? = nil) -> CALayer {
    let scaleX: CGFloat = CGFloat(ResourcesDimensions.speechBubbleCorners) / 10.0
    let scaleY: CGFloat = CGFloat(ResourcesDimensions.speechBubbleCorners) / 10.0
    let layer = CALayer()
    layer.addSublayer({
        let sublayer = CAShapeLayer()
        let path = CGMutablePath()
        //M 10.0, 0.0
        path.move(to: CGPoint(x: 10.0 * scaleX, y: 0.0 * scaleY))
        //A 10.0, 10.0, 0.0, 0.0, 0.0, 0.0, 10.0
        path.arcTo(radius: CGSize(width: 10.0 * scaleX, height: 10.0 * scaleY), rotation: 0.0, largeArcFlag: false, sweepFlag: false, end: CGPoint(x: 0.0 * scaleX, y: 10.0 * scaleY))
        //L 10.0, 10.0
        path.addLine(to: CGPoint(x: 10.0 * scaleX, y: 10.0 * scaleY))
        //Z 
        sublayer.path = path
        sublayer.fillColor = ResourcesColors.speechBubbleSelf.cgColor
        return sublayer
    }())
    layer.bounds.size = CGSize(width: 0.0, height: 0.0)
    layer.scaleOverResize = true
    return layer
}
static func zComplexDrawablePart7(_ view: UIView? = nil) -> CALayer {
    let scaleX: CGFloat = CGFloat(ResourcesDimensions.speechBubbleCornersPlusTail) / 150.0
    let scaleY: CGFloat = CGFloat(ResourcesDimensions.speechBubbleCornersPlusTail) / 150.0
    let layer = CALayer()
    layer.addSublayer({
        let sublayer = CAShapeLayer()
        let path = CGMutablePath()
        //M 0.0, 100.0
        path.move(to: CGPoint(x: 0.0 * scaleX, y: 100.0 * scaleY))
        //C 7.67309143, 100.0, 14.1935201, 100.346373, 20.500756, 99.0996492
        path.addCurve(to: CGPoint(x: 20.500756 * scaleX, y: 99.0996492 * scaleY), control1: CGPoint(x: 7.67309143 * scaleX, y: 100.0 * scaleY), control2: CGPoint(x: 14.1935201 * scaleX, y: 100.346373 * scaleY))
        //C 43.6628959, 129.872031, 94.1698247, 146.306561, 150.320843, 150.792562
        path.addCurve(to: CGPoint(x: 150.320843 * scaleX, y: 150.792562 * scaleY), control1: CGPoint(x: 43.6628959 * scaleX, y: 129.872031 * scaleY), control2: CGPoint(x: 94.1698247 * scaleX, y: 146.306561 * scaleY))
        //C 113.168693, 130.799632, 87.2808993, 98.5054948, 81.0808824, 68.6524321
        path.addCurve(to: CGPoint(x: 81.0808824 * scaleX, y: 68.6524321 * scaleY), control1: CGPoint(x: 113.168693 * scaleX, y: 130.799632 * scaleY), control2: CGPoint(x: 87.2808993 * scaleX, y: 98.5054948 * scaleY))
        //C 94.1277117, 51.7595331, 100.0, 23.9957121, 100.0, 0.0
        path.addCurve(to: CGPoint(x: 100.0 * scaleX, y: 0.0 * scaleY), control1: CGPoint(x: 94.1277117 * scaleX, y: 51.7595331 * scaleY), control2: CGPoint(x: 100.0 * scaleX, y: 23.9957121 * scaleY))
        //L 0.0, 0.0
        path.addLine(to: CGPoint(x: 0.0 * scaleX, y: 0.0 * scaleY))
        //L 0.0, 100.0
        path.addLine(to: CGPoint(x: 0.0 * scaleX, y: 100.0 * scaleY))
        //Z 
        sublayer.path = path
        sublayer.fillColor = ResourcesColors.speechBubbleSelf.cgColor
        return sublayer
    }())
    layer.bounds.size = CGSize(width: 0.0, height: 0.0)
    layer.scaleOverResize = true
    return layer
}

}

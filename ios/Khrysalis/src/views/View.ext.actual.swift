//Stub file made with Khrysalis 2 (by Lightning Kite)
import UIKit

//--- View
public typealias View = UIView

//--- View.backgroundDrawable
public extension View {
    static var appForegroundColor: UIColor? = nil
    static var appAccentColor: UIColor? = nil
    var backgroundDrawable: Drawable? {
        set(value){
            if let value = value {
                backgroundLayer = value.makeLayer(self)
            } else {
                backgroundLayer = nil
            }
        }
        get {
            if let backgroundLayer = backgroundLayer {
                return Drawable { _ in backgroundLayer }
            } else {
                return nil
            }
        }
    }
    
    var rotation: Float{
        set(value){
            self.transform = CGAffineTransform(rotationAngle: CGFloat(Double(value) * (Double.pi / 180.0)))
        }
        get{
            let rad = atan2(self.transform.b, self.transform.a)
            return Float(rad * (180 / .pi))
        }
    }
    
    static let VISIBLE = ViewVisibility.VISIBLE
    static let INVISIBLE = ViewVisibility.INVISIBLE
    static let GONE = ViewVisibility.GONE
    
    var visibility: ViewVisibility {
        get {
            if !includeInLayout {
                return .GONE
            } else if isHidden {
                return .INVISIBLE
            } else {
                return .VISIBLE
            }
        }
        set(value){
            switch value {
            case .GONE:
                includeInLayout = false
                isHidden = true
            case .INVISIBLE:
                includeInLayout = true
                isHidden = true
            case .VISIBLE:
                includeInLayout = true
                isHidden = false
            }
            setNeedsLayout()
        }
    }
}

public enum ViewVisibility {
    case VISIBLE, INVISIBLE, GONE
}

public extension UIView {

    //--- View.postInvalidate
    func postInvalidate() {
        setNeedsDisplay()
    }
    
    //--- View.invalidate
    func invalidate() {
        setNeedsDisplay()
    }
}

//--- View.backgroundResource
//--- View.setBackgroundColorResource(ColorResource)
public extension View {
    var backgroundResource: Drawable? {
        set(value){
            backgroundDrawable = value
        }
        get {
            return backgroundDrawable
        }
    }
    func setBackgroundColorResource(_ color: ColorResource) {
        backgroundColor = color
    }
    func setBackgroundColorResource(color: ColorResource) {
        backgroundColor = color
    }
}

//--- View.onClick(()->Unit)
//--- View.onClick(Long, ()->Unit)
//--- View.onLongClick(()->Unit)
public extension UIView {
    @objc func setOnClickListener(_ action: @escaping (UIView)->Void) {
        self.isUserInteractionEnabled = true
        let recognizer = UITapGestureRecognizer().addAction(until: removed) { [weak self] in
            if let self = self { action(self) }
        }
        retain(as: "onClickRecognizer", item: recognizer, until: removed)
        self.addGestureRecognizer(recognizer)
    }
    @objc func setOnLongClickListener(_ action: @escaping (UIView)->Bool) {
        self.isUserInteractionEnabled = true
        let recognizer = UILongPressGestureRecognizer()
        recognizer.addAction(until: removed) { [unowned recognizer, weak self] in
            if recognizer.state == .ended, let self = self {
                let _ = action(self)
            }
        }
        retain(as: "onLongClickRecognizer", item: recognizer, until: removed)
        self.addGestureRecognizer(recognizer)
    }

    @objc func onClick(action: @escaping ()->Void) {
        onClick(disabledMilliseconds: 500, action: action)
    }
    @objc func onClick(disabledMilliseconds: Int64, action: @escaping ()->Void) {
        self.isUserInteractionEnabled = true
        var lastActivated = Date()
        let recognizer = UITapGestureRecognizer().addAction(until: removed) {
            if Date().timeIntervalSince(lastActivated) > Double(disabledMilliseconds)/1000.0 {
                action()
                lastActivated = Date()
            }
        }
        retain(as: "onClickRecognizer", item: recognizer, until: removed)
        self.addGestureRecognizer(recognizer)
    }
    @objc func onLongClick(action: @escaping ()->Void) {
        self.isUserInteractionEnabled = true
        let recognizer = UILongPressGestureRecognizer()
        recognizer.addAction(until: removed) { [unowned recognizer, weak self] in
            if recognizer.state == .ended {
                action()
            }
        }
        retain(as: "onLongClickRecognizer", item: recognizer, until: removed)
        self.addGestureRecognizer(recognizer)
    }
    @objc func onLongClickWithGR(action: @escaping (UILongPressGestureRecognizer)->Void) {
        self.isUserInteractionEnabled = true
        let recognizer = UILongPressGestureRecognizer()
        recognizer.addAction(until: removed) { [unowned recognizer, weak self] in
            action(recognizer)
        }
        retain(as: "onLongClickRecognizer", item: recognizer, until: removed)
        self.addGestureRecognizer(recognizer)
    }
}
extension UIButton {
    @objc override public func onClick(action: @escaping ()->Void) {
        onClick(disabledMilliseconds: 500, action: action)
    }
    @objc override public func onClick(disabledMilliseconds: Int64, action: @escaping ()->Void) {
        var lastActivated = Date()
        self.addAction {
            if Date().timeIntervalSince(lastActivated) > Double(disabledMilliseconds)/1000.0 {
                action()
                lastActivated = Date()
            }
        }
    }
}

//--- UIView.performClick
public extension UIControl {
    @objc override func performClick(){
        self.sendActions(for: .primaryActionTriggered)
    }
}
public extension UIView {
    @objc func performClick(){
        TODO()
    }
}

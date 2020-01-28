//Stub file made with Kwift 2 (by Lightning Kite)
import UIKit

//--- View
public typealias View = UIView

//--- View.backgroundDrawable
public extension View {
    var backgroundDrawable: Drawable? {
        set(value){
            if let value = value {
                backgroundLayer = value(self)
            } else {
                backgroundLayer = nil
            }
        }
        get {
            if let backgroundLayer = backgroundLayer {
                return { _ in backgroundLayer }
            } else {
                return nil
            }
        }
    }
}

public extension UIView {

    //--- View.postInvalidate
    func postInvalidate() {
        setNeedsDisplay()
    }
    
    //--- View.invalidate
    func invalidate(){
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
    func setBackgroundColorResource(_ colorResource: ColorResource) {
        backgroundColor = colorResource
    }
}

//--- View.onClick(()->Unit)
//--- View.onClick(Long, ()->Unit)
//--- View.onLongClick(()->Unit)
public extension UIView {
    @objc func onClick(_ action: @escaping ()->Void) {
        onClick(disabledMilliseconds: 500, action)
    }
    @objc func onClick(disabledMilliseconds: Int64, _ action: @escaping ()->Void) {
        self.isUserInteractionEnabled = true
        var lastActivated = Date()
        let recognizer = UITapGestureRecognizer().addAction {
            if Date().timeIntervalSince(lastActivated) > Double(disabledMilliseconds)/1000.0 {
                action()
                lastActivated = Date()
            }
        }
        retain(as: "onClickRecognizer", item: recognizer)
        self.addGestureRecognizer(recognizer)
    }
    @objc func onLongClick(_ action: @escaping ()->Void) {
        self.isUserInteractionEnabled = true
        let recognizer = UILongPressGestureRecognizer().addAction { [weak self] in
            action()
        }
        retain(as: "onLongClickRecognizer", item: recognizer)
        self.addGestureRecognizer(recognizer)
    }
    @objc func onLongClickWithGR(_ action: @escaping (UILongPressGestureRecognizer)->Void) {
        self.isUserInteractionEnabled = true
        let recognizer = UILongPressGestureRecognizer()
        recognizer.addAction { [unowned recognizer, weak self] in
            action(recognizer)
        }
        retain(as: "onLongClickRecognizer", item: recognizer)
        self.addGestureRecognizer(recognizer)
    }
}
extension UIButton {
    @objc override public func onClick(_ action: @escaping ()->Void) {
        onClick(disabledMilliseconds: 500, action)
    }
    @objc override public func onClick(disabledMilliseconds: Int64, _ action: @escaping ()->Void) {
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
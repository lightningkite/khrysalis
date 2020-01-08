//Stub file made with Kwift 2 (by Lightning Kite)
import Foundation
import UIKit


//--- TextView.textResource
//--- TextView.textString
//--- TextView.setColor(ColorResource)
//--- ToggleButton.textResource
//--- ToggleButton.textString

private let viewLetterSpacing = ExtensionProperty<UIView, CGFloat>()
private let viewAllCaps = ExtensionProperty<UIView, Bool>()
public var defaultLetterSpacing: CGFloat = 0

public extension UILabel {
    var letterSpacing: CGFloat {
        get{
            return viewLetterSpacing.get(self) ?? defaultLetterSpacing
        }
        set(value){
            viewLetterSpacing.set(self, value)
            let current = textString
            textString = current
        }
    }
    var textAllCaps: Bool {
        get{
            return viewAllCaps.get(self) ?? false
        }
        set(value){
            viewAllCaps.set(self, value)
            let current = textString
            textString = current
        }
    }
    
    var textResource: String {
        get {
            return text ?? ""
        }
        set(value) {
            textString = value
        }
    }
    var textString: String {
        get {
            return text ?? ""
        }
        set(value) {
            var toSet = value
            if textAllCaps {
                toSet = toSet.toUpperCase()
            }
            self.attributedText = NSAttributedString(string: toSet, attributes: [.kern: letterSpacing * font.pointSize])
        }
    }

    func setColor(color: ColorResource){
        textColor = color
    }

    func setColor(_ color: ColorResource){
        textColor = color
    }
}

public extension UITextView {
    var letterSpacing: CGFloat {
        get{
            return viewLetterSpacing.get(self) ?? defaultLetterSpacing
        }
        set(value){
            viewLetterSpacing.set(self, value)
            let current = textString
            textString = current
        }
    }
    var textAllCaps: Bool {
        get{
            return viewAllCaps.get(self) ?? false
        }
        set(value){
            viewAllCaps.set(self, value)
            let current = textString
            textString = current
        }
    }
    var textResource: String {
        get {
            return text ?? ""
        }
        set(value) {
            textString = value
        }
    }
    var textString: String {
        get {
            return text ?? ""
        }
        set(value) {
            var toSet = value
            if textAllCaps {
                toSet = toSet.toUpperCase()
            }
            self.attributedText = NSAttributedString(string: toSet, attributes: [.kern: letterSpacing * (font?.pointSize ?? 12)])
        }
    }
}

public extension UITextField {
    var letterSpacing: CGFloat {
        get{
            return viewLetterSpacing.get(self) ?? defaultLetterSpacing
        }
        set(value){
            viewLetterSpacing.set(self, value)
            let current = textString
            textString = current
        }
    }
    var textAllCaps: Bool {
        get{
            return viewAllCaps.get(self) ?? false
        }
        set(value){
            viewAllCaps.set(self, value)
            let current = textString
            textString = current
        }
    }
    var textResource: String {
        get {
            return text ?? ""
        }
        set(value) {
            textString = value
        }
    }
    var textString: String {
        get {
            return text ?? ""
        }
        set(value) {
            var toSet = value
            if textAllCaps {
                toSet = toSet.toUpperCase()
            }
            self.attributedText = NSAttributedString(string: toSet, attributes: [.kern: letterSpacing * (font?.pointSize ?? 12)])
        }
    }
}

public extension UIButton {
    var letterSpacing: CGFloat {
        get{
            return viewLetterSpacing.get(self) ?? defaultLetterSpacing
        }
        set(value){
            viewLetterSpacing.set(self, value)
            let current = textString
            textString = current
        }
    }
    var textAllCaps: Bool {
        get{
            return viewAllCaps.get(self) ?? false
        }
        set(value){
            viewAllCaps.set(self, value)
            let current = textString
            textString = current
        }
    }
    @objc var textResource: String {
        get {
            return title(for: .normal) ?? ""
        }
        set(value) {
            textString = value
        }
    }
    @objc var textString: String {
        get {
            return title(for: .normal) ?? ""
        }
        set(value) {
            var toSet = value
            if textAllCaps {
                toSet = toSet.toUpperCase()
            }
            setAttributedTitle(
                NSAttributedString(string: toSet, attributes: [.kern: letterSpacing * (titleLabel?.font.pointSize ?? 12)]),
                for: .normal
            )
        }
    }
}








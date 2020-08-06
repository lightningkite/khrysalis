//Stub file made with Khrysalis 2 (by Lightning Kite)
import Foundation
import UIKit


//--- TextView.textResource
//--- TextView.textString
//--- TextView.setTextColorResource(ColorResource)
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
            notifyParentSizeChanged()
        }
    }
    var textString: String {
        get {
            return text ?? ""
        }
        set(value) {
            var toSet = value
            if textAllCaps {
                toSet = toSet.uppercased()
            }
            self.attributedText = NSAttributedString(string: toSet, attributes: [.kern: letterSpacing * font.pointSize])
            notifyParentSizeChanged()
        }
    }

    func setTextColorResource(color: ColorResource){
        textColor = color
    }

    func setTextColorResource(_ color: ColorResource){
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
                toSet = toSet.uppercased()
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
                toSet = toSet.uppercased()
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
    @objc var text: String {
        get {
            return title(for: .normal) ?? ""
        }
        set(value) {
            textString = value
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
                toSet = toSet.uppercased()
            }
            setAttributedTitle(
                NSAttributedString(string: toSet, attributes: [.kern: letterSpacing * (titleLabel?.font.pointSize ?? 12)]),
                for: .normal
            )
        }
    }
}

public extension HasLabelView where Self: UIView {
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
    var text: String {
        get {
            return self.labelView.textResource
        }
        set(value) {
            self.labelView.textString = value
        }
    }
    var textResource: String {
        get {
            return self.labelView.textResource
        }
        set(value) {
            self.labelView.textString = value
        }
    }
    var textString: String {
        get {
            return self.labelView.textString
        }
        set(value) {
            self.labelView.textString = value
        }
    }
}

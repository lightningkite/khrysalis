import UIKit

public class UITextFieldPadded: UITextField {

    public var padding = UIEdgeInsets(top: 0, left: 0, bottom: 0, right: 0) {
        didSet {
            self.notifyParentSizeChanged()
            self.setNeedsDisplay()
        }
    }
    public var compoundPadding: CGFloat = 8 {
        didSet {
            self.notifyParentSizeChanged()
            self.setNeedsDisplay()
        }
    }
    
    private var totalPadding: UIEdgeInsets {
        get {
            return UIEdgeInsets(
                top: padding.top,
                left: leftView == nil ? padding.left : padding.left + leftView!.bounds.size.width + compoundPadding,
                bottom: padding.bottom,
                right: rightView == nil ? padding.right : padding.right + rightView!.bounds.size.width + compoundPadding
            )
        }
    }

    override public func textRect(forBounds bounds: CGRect) -> CGRect {
        return bounds.inset(by: totalPadding)
    }

    override public func placeholderRect(forBounds bounds: CGRect) -> CGRect {
        return bounds.inset(by: totalPadding)
    }

    override public func editingRect(forBounds bounds: CGRect) -> CGRect {
        return bounds.inset(by: totalPadding)
    }
    
    override public func leftViewRect(forBounds bounds: CGRect) -> CGRect {
        guard let leftView = leftView else { return bounds }
        let size = leftView.bounds.size
        return CGRect(bounds.left + padding.left, bounds.centerY() - size.height/2, bounds.left + padding.left + size.width, bounds.centerY() + size.height/2)
    }
    
    override public func rightViewRect(forBounds bounds: CGRect) -> CGRect {
        guard let rightView = rightView else { return bounds }
        let size = rightView.bounds.size
        return CGRect(bounds.right - padding.right - size.width, bounds.centerY()-size.height/2, bounds.right - padding.right, bounds.centerY()+size.height/2)
    }
    
    override public func layoutSubviews() {
        super.layoutSubviews()
        leftView?.frame = leftViewRect(forBounds: self.bounds)
        rightView?.frame = rightViewRect(forBounds: self.bounds)
    }
}
public class UIAutoCompleteTextFieldPadded: UIAutoCompleteTextField {
    
    public var padding = UIEdgeInsets(top: 0, left: 0, bottom: 0, right: 0) {
        didSet {
            self.notifyParentSizeChanged()
        }
    }
    public var compoundPadding: CGFloat = 8 {
        didSet {
            self.notifyParentSizeChanged()
        }
    }
    
    private var totalPadding: UIEdgeInsets {
        get {
            return UIEdgeInsets(
                top: padding.top,
                left: leftView == nil ? padding.left : padding.left + leftView!.bounds.size.width + compoundPadding,
                bottom: padding.bottom,
                right: rightView == nil ? padding.right : padding.right + rightView!.bounds.size.width + compoundPadding
            )
        }
    }

    override public func textRect(forBounds bounds: CGRect) -> CGRect {
        return bounds.inset(by: totalPadding)
    }

    override public func placeholderRect(forBounds bounds: CGRect) -> CGRect {
        return bounds.inset(by: totalPadding)
    }

    override public func editingRect(forBounds bounds: CGRect) -> CGRect {
        return bounds.inset(by: totalPadding)
    }
    
    override public func leftViewRect(forBounds bounds: CGRect) -> CGRect {
        guard let leftView = leftView else { return bounds }
        let size = leftView.bounds.size
        return CGRect(bounds.left + padding.left, bounds.centerY() - size.height/2, bounds.left + padding.left + size.width, bounds.centerY() + size.height/2)
    }
    
    override public func rightViewRect(forBounds bounds: CGRect) -> CGRect {
        guard let rightView = rightView else { return bounds }
        let size = rightView.bounds.size
        return CGRect(bounds.right - padding.right - size.width, bounds.centerY()-size.height/2, bounds.right - padding.right, bounds.centerY()+size.height/2)
    }
}

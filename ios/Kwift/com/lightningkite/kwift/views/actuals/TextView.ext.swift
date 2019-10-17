//
//  UITextView.swift
//  Alamofire
//
//  Created by Joseph Ivie on 10/9/19.
//

import Foundation
import UIKit


public extension UILabel {
    var textResource: String {
        get {
            return text ?? ""
        }
        set(value) {
            text = value
        }
    }
    var textString: String {
        get {
            return text ?? ""
        }
        set(value) {
            text = value
        }
    }
}

public extension UITextView {
    var textResource: String {
        get {
            return text ?? ""
        }
        set(value) {
            text = value
        }
    }
    var textString: String {
        get {
            return text ?? ""
        }
        set(value) {
            text = value
        }
    }
}

public extension UITextField {
    var textResource: String {
        get {
            return text ?? ""
        }
        set(value) {
            text = value
        }
    }
    var textString: String {
        get {
            return text ?? ""
        }
        set(value) {
            text = value
        }
    }
}

public extension UIButton {
    var textResource: String {
        get {
            return title(for: .normal) ?? ""
        }
        set(value) {
            setTitle(value, for: .normal)
        }
    }
    var textString: String {
        get {
            return title(for: .normal) ?? ""
        }
        set(value) {
            setTitle(value, for: .normal)
        }
    }
}

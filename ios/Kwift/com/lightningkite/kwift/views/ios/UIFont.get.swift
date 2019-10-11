//
//  UIFont.get.swift
//  Lifting Generations
//
//  Created by Joseph Ivie on 3/20/19.
//  Copyright © 2019 Joseph Ivie. All rights reserved.
//

import Foundation
import UIKit


extension UIFont {

    static public func list() {
        for family in UIFont.familyNames.sorted() {
            let names = UIFont.fontNames(forFamilyName: family)
            print("Family: \(family) Font names: \(names)")
        }
    }

    static var customFont: String?

    static public func get(size: CGFloat, style: Array<String>) -> UIFont {
        if style.contains("bold") {
            if style.contains("italic") {
                if let customFont = customFont {
                    return UIFont(name: "\(customFont)-BoldItalic", size: size) ?? UIFont.boldSystemFont(ofSize: size)
                } else {
                    return UIFont.boldSystemFont(ofSize: size)
                }
            } else {
                if let customFont = customFont {
                    return UIFont(name: "\(customFont)-Bold", size: size) ?? UIFont.boldSystemFont(ofSize: size)
                } else {
                    return UIFont.boldSystemFont(ofSize: size)
                }
            }
        } else {
            if style.contains("italic") {
                if let customFont = customFont {
                    return UIFont(name: "\(customFont)-Italic", size: size) ?? UIFont.italicSystemFont(ofSize: size)
                } else {
                    return UIFont.italicSystemFont(ofSize: size)
                }
            } else {
                if let customFont = customFont {
                    return UIFont(name: "\(customFont)-Regular", size: size) ?? UIFont.systemFont(ofSize: size)
                } else {
                    return UIFont.systemFont(ofSize: size)
                }
            }
        }
    }
}

public extension String {
    func attributedWithColor(_ color: UIColor) -> NSAttributedString {
        return NSAttributedString(string: self, attributes: [NSAttributedString.Key.foregroundColor: color])
    }
}

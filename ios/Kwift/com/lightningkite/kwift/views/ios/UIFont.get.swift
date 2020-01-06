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
            print("Family: '\(family)' Font names: \(names)")
        }
    }
    
    public enum WeightClass {
        case Light, Regular, Bold
    }
    
    public struct FontStyle: Hashable {
        let weight: WeightClass
        let italic: Bool
        
        public init(weight: WeightClass, italic: Bool){
            self.weight = weight
            self.italic = italic
        }
        
        public init(_ weight: WeightClass, _ italic: Bool){
            self.weight = weight
            self.italic = italic
        }
    }
    
    static public var customFonts: Dictionary<FontStyle, String> = [:]
    static public func customFont(weight: UIFont.WeightClass, italic: Bool, name: String){
        customFonts[FontStyle(weight, italic)] = name
    }
    
    static public func get(size: CGFloat, weight: UIFont.WeightClass, italic: Bool) -> UIFont {
        if let fontName = customFonts[FontStyle(weight, italic)] {
            return UIFont(name: fontName, size: size)!
        } else {
            switch(weight){
            case .Light:
                return UIFont.systemFont(ofSize: size)
            case .Regular:
                if italic {
                    return UIFont.italicSystemFont(ofSize: size)
                } else {
                    return UIFont.systemFont(ofSize: size)
                }
            case .Bold:
                return UIFont.boldSystemFont(ofSize: size)
            @unknown default:
                return UIFont.systemFont(ofSize: size)
            }
        }
    }

    static public func get(size: CGFloat, style: Array<String>) -> UIFont {
        var weightClass = WeightClass.Regular
        if style.contains("bold") {
            weightClass = .Bold
        }
        if style.contains("light") {
            weightClass = .Light
        }
        let italic = style.contains("italic")
        return UIFont.get(size: size, weight: weightClass, italic: italic)
    }
}

public extension String {
    func attributedWithColor(_ color: UIColor) -> NSAttributedString {
        return NSAttributedString(string: self, attributes: [NSAttributedString.Key.foregroundColor: color])
    }
}

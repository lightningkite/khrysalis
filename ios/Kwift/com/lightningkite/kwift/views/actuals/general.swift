//
//  general.swift
//  KwiftTemplate
//
//  Created by Joseph Ivie on 8/20/19.
//  Copyright © 2019 Joseph Ivie. All rights reserved.
//

import Foundation
import UIKit

public typealias View = UIView

public class ViewDependency {
    public unowned let parentViewController: UIViewController
    public init(_ parentViewController: UIViewController){
        self.parentViewController = parentViewController
    }
    public func getString(_ reference: StringReference) -> String {
        return reference
    }
    public func getColor(_ reference: ColorResource) -> UIColor {
        return reference
    }
    public var displayMetrics: DisplayMetrics {
        return DisplayMetrics(
            density: Float(UIScreen.main.scale),
            scaledDensity: Float(UIScreen.main.scale),
            widthPixels: Int32(UIScreen.main.bounds.width * UIScreen.main.scale),
            heightPixels: Int32(UIScreen.main.bounds.height * UIScreen.main.scale)
        )
    }
}

public struct DisplayMetrics {
    public let density: Float
    public let scaledDensity: Float
    public let widthPixels: Int32
    public let heightPixels: Int32
}

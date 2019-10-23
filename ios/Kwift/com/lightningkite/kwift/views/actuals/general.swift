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
}

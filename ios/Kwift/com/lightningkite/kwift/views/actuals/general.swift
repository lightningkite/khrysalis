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
    public init(){}
    public func getString(_ reference: StringReference) -> String {
        return reference
    }
}

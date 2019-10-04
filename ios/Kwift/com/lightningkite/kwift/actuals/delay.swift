//
//  delay.swift
//  Lifting Generations
//
//  Created by Joseph Ivie on 3/4/19.
//  Copyright © 2019 Joseph Ivie. All rights reserved.
//

import Foundation
import UIKit


public func delay(milliseconds: Int64, action: @escaping ()->Void) {
    if milliseconds == 0 {
        action()
    } else {
        DispatchQueue.main.asyncAfter(deadline: .now() + DispatchTimeInterval.milliseconds(Int(milliseconds)), execute: action)
    }
}

public func delay(_ milliseconds: Int64, action: @escaping ()->Void) {
    delay(milliseconds: milliseconds, action: action)
}

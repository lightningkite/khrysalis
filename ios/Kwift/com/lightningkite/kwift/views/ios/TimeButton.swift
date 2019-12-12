//
//  DateButton.swift
//  Lifting Generations
//
//  Created by Joseph Ivie on 5/13/19.
//  Copyright © 2019 Joseph Ivie. All rights reserved.
//

import Foundation
import UIKit


public class TimeButton : DateButton {

    var minuteInterval: Int = 1
    var hourInterval: Int = 1
    
    override public func commonInit() {
        super.commonInit()
        mode = .time
        let format = DateFormatter()
        format.dateStyle = .none;
        format.timeStyle = .short;
        self.format = format
    }
    
    
}

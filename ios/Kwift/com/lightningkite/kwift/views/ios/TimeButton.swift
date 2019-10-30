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
    
    public var onDateEntered = StandardEvent<Date>()
    
    override public func commonInit() {
        super.commonInit()
        mode = .time
        let format = DateFormatter()
        format.dateStyle = .none;
        format.timeStyle = .medium;
        self.format = format
    }
}

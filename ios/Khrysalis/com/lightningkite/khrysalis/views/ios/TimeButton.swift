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

    private var internalMinuteInterval: Int = 1
    public var minuteInterval: Int{
        get{
            return internalMinuteInterval
        }
        set(value){
            internalMinuteInterval = value
            picker.minuteInterval = value
        }
    }
    
    override public func commonInit() {
        super.commonInit()
        mode = .time
        picker.minuteInterval = minuteInterval
        let format = DateFormatter()
        format.dateStyle = .none;
        format.timeStyle = .short;
        self.format = format
    }
    
    
}

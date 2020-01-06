//
//  Serialization.swift
//  PennyProfit
//
//  Created by Joseph Ivie on 1/2/19.
//  Copyright © 2019 Shane Thompson. All rights reserved.
//

import Foundation

public protocol OptionalProtocol {
    var finalValue: Any { get }
}
extension Optional: OptionalProtocol {
    public var finalValue: Any {
        get {
            switch(self){
            case Optional.none:
                return NSNull()
            case let Optional.some(wrapped):
                if let wrapped = wrapped as? OptionalProtocol {
                    return wrapped.finalValue
                } else {
                    return wrapped
                }
            }
        }
    }
}

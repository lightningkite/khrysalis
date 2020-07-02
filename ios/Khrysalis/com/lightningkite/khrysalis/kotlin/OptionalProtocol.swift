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

public protocol OptionalConvertible {
    associatedtype Wrapped
    var asOptional: Optional<Wrapped> { get }
}

extension Optional: OptionalConvertible {
    public var asOptional: Optional<Wrapped> { return self }
}


public func invertOptional<T>(_ value: T??) -> T?? {
    if let inner = value {
        if let value = inner {
            return .some(.some(value))
        } else {
            return nil
        }
    } else {
        return .some(nil)
    }
}

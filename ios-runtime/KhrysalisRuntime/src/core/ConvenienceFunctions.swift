//
//  Kotlin.swift
//

import Foundation

public func TODO(reason: String = "") -> Never {
    //Throw execption
    fatalError("TODO: \(reason)")
}

public func run<T>(_ action: () throws->T) rethrows ->T {
    return try action()
}

public func also<T>(_ value: T, _ action: (inout T)->Void)->T {
    var value = value
    action(&value)
    return value
}

public func takeIf<T>(_ value: T, _ condition: (T)->Bool)->T? {
    if condition(value) {
        return value
    }
    return nil
}
public func takeIf<T>(_ value: T?, _ condition: (T)->Bool)->T? {
    if let value = value, condition(value) {
        return value
    }
    return nil
}
public func takeUnless<T>(_ value: T, _ condition: (T)->Bool)->T? {
    if !condition(value) {
        return value
    }
    return nil
}
public func takeUnless<T>(_ value: T?, _ condition: (T)->Bool)->T? {
    if let value = value, !condition(value) {
        return value
    }
    return nil
}

//
//  extensionProperty.swift
//  KhrysalisTemplate
//
//  Created by Joseph Ivie on 8/21/19.
//  Copyright © 2019 Joseph Ivie. All rights reserved.
//

import Foundation

public class ExtensionProperty<On: AnyObject, T> {
    
    public init(){}

    public class T {
        var value: T
        init(value: T){
            self.value = value
        }
    }
    private var table = NSMapTable<On, T>(keyOptions: .weakMemory, valueOptions: .strongMemory)
    public func get(_ from: On) -> T? {
        return table.object(forKey: from)?.value
    }
    public func getOrPut(_ from: On, _ generate: ()->T) -> T {
        if let value = table.object(forKey: from)?.value { return value }
        let generated = generate()
        let box = Box(value: generated)
        table.setObject(box, forKey: from)
        return generated
    }
    public func modify(_ from: On, defaultValue:T? = nil, modifier: (T)->Void) {
        if let box = table.object(forKey: from) {
            modifier(box)
        } else if let defaultValue = defaultValue {
            let box = Box(value: defaultValue)
            modifier(box)
            table.setObject(box, forKey: from)
        }
    }
    public func set(_ from: On, _ value: T?) {
        if let value = value {
            let box = Box(value: value)
            table.setObject(box, forKey: from)
        } else {
            table.removeObject(forKey: from)
        }
    }
}

//
//  extensionProperty.swift
//  KwiftTemplate
//
//  Created by Joseph Ivie on 8/21/19.
//  Copyright © 2019 Joseph Ivie. All rights reserved.
//

import Foundation

public class ExtensionProperty<On: AnyObject, T> {

    public class Box<T> {
        var value: T
        init(value: T){
            self.value = value
        }
    }
    private var table = NSMapTable<On, Box<T>>(keyOptions: .weakMemory, valueOptions: .strongMemory)
    public func get(_ from: On) -> T? {
        return table.object(forKey: from)?.value
    }
    public func modify(_ from: On, defaultValue:T? = nil, modifier: (Box<T>)->Void) {
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

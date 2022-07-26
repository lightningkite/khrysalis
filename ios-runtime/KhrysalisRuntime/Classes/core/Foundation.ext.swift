//
//  Foundation.ext.swift
//  KhrysalisRuntime
//
//  Created by Joseph on 7/26/22.
//

import Foundation

public extension UserDefaults {
    func clear() {
        for k in dictionaryRepresentation().keys {
            removeObject(forKey: k)
        }
    }
}

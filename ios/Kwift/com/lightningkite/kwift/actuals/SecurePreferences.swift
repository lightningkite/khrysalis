//
//  SecurePreferences.swift
//  Lifting Generations
//
//  Created by Joseph Ivie on 3/13/19.
//  Copyright © 2019 Joseph Ivie. All rights reserved.
//

import Foundation
import KeychainAccess


public enum SecurePreferences {
    
    public static var keychain = Keychain(service: (Bundle.main.bundleIdentifier ?? "com.lightningkite.kwift.unknownApp") + ".securePreferences")
    
    public static func setKeychainAccessGroup(_ name: String) {
        print("Access name is now " + name)
        keychain = Keychain(accessGroup: name)
    }
    
    public static func set<T: Codable>(_ key: String, _ value: T) { set(key: key, value: value) }
    public static func get<T: Codable>(_ key: String) -> T? { return get(key: key) }
    public static func remove(_ key: String) -> Void { remove(key: key) }
    
    public static func set<T: Codable>(key: String, value: T) {
        if let string = try? value.toJsonString() {
            keychain[key] = string
        }
    }
    
    public static func get<T: Codable>(key: String) -> T? {
        if let string = keychain[key],
            let parsed = try? T.fromJsonString(string) {
            return parsed
        }
        return nil
    }
    
    public static func remove(key: String) -> Void {
        try? keychain.remove(key)
    }
    
}

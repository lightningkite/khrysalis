//
//  Preferences.swift
//  Lifting Generations
//
//  Created by Joseph Ivie on 3/13/19.
//  Copyright © 2019 Joseph Ivie. All rights reserved.
//

import Foundation


public enum Preferences {
    
    public static func clear() {
        for k in UserDefaults.standard.dictionaryRepresentation().keys {
            UserDefaults.standard.removeObject(forKey: k)
        }
    }
    public static func set<T: Codable>(_ key: String, _ value: T) { set(key: key, value: value) }
    public static func get<T: Codable>(_ key: String) -> T? { return get(key: key) }
    public static func remove(_ key: String) -> Void { remove(key: key) }
    
    public static func set<T: Codable>(key: String, value: T) {
        if let string = try? value.toJsonString() {
            UserDefaults.standard.setValue(string, forKey: key)
        }
    }
    
    public static func get<T: Codable>(key: String) -> T? {
        if let string = UserDefaults.standard.string(forKey: key),
            let parsed = try? T.fromJsonString(string) {
            
            return parsed
        }
        return nil
    }
    
    public static func remove(key: String) -> Void {
        UserDefaults.standard.removeObject(forKey: key)
    }
    
}

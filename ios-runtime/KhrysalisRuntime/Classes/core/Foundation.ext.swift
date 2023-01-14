//
//  Foundation.ext.swift
//  KhrysalisRuntime
//
//  Created by Joseph on 7/26/22.
//

import Foundation
import Security

extension UserDefaults: UserDefaultsProtocol {
    public func clear() {
        for k in dictionaryRepresentation().keys {
            removeObject(forKey: k)
        }
    }
}

public protocol UserDefaultsProtocol {
    func set(_ value: Any?, forKey: String)
    func string(forKey: String) -> String?
    func removeObject(forKey: String)
    func clear()
}

public class KeychainUserDefaults: UserDefaultsProtocol {
    private let lock = NSLock()
    public let namespace: String
    public init(namespace: String) {
        self.namespace = namespace
    }
    
    public static let standard = KeychainUserDefaults(namespace: Bundle.main.bundleIdentifier!)
    public static var shared: KeychainUserDefaults { standard }
    
    private func prefixedKey(_ key: String) -> String {
        return namespace + "/" + key
    }
    
    public func set(_ value: Any?, forKey: String) {
        lock.lock()
        defer { lock.unlock() }
        var data: Data
        switch value {
        case let x as String:
            data = x.data(using: .utf8)!
        case let x as Data:
            data = x
        default:
            fatalError()
        }
        SecItemDelete([
            kSecClass       : kSecClassGenericPassword,
            kSecAttrAccount : prefixedKey(forKey)
        ] as CFDictionary)
        
        let result = SecItemAdd([
            kSecClass       : kSecClassGenericPassword,
            kSecAttrAccount : prefixedKey(forKey),
            kSecValueData   : data,
        ] as CFDictionary, nil)
        
        if result != noErr {
            fatalError("Cannot write to Keychain")
        }
    }
    
    public func data(forKey: String) -> Data? {
        lock.lock()
        defer { lock.unlock() }
        let prefixedKey = prefixedKey(forKey)
        
        var result: AnyObject?
        
        let lastResultCode = withUnsafeMutablePointer(to: &result) {
          SecItemCopyMatching([
            kSecClass       : kSecClassGenericPassword,
            kSecAttrAccount : prefixedKey,
            kSecReturnData : kCFBooleanTrue!,
          kSecMatchLimit  : kSecMatchLimitOne
        ] as CFDictionary, UnsafeMutablePointer($0))
        }
                
        if lastResultCode == noErr, let result = result {
          return result as? Data
        }
        
        return nil
    }
    public func string(forKey: String) -> String? {
        if let data = data(forKey: forKey) {
            if let currentString = String(data: data, encoding: .utf8) {
                return currentString
            }
        }
        return nil
    }
    
    public func removeObject(forKey: String) {
        lock.lock()
        defer { lock.unlock() }
        let result = SecItemDelete([
            kSecClass       : kSecClassGenericPassword,
            kSecAttrAccount : prefixedKey(forKey)
        ] as CFDictionary)
        if result != noErr && result != errSecItemNotFound {
            fatalError("Cannot write to Keychain")
        }
    }
    
    public func clear() {
        lock.lock()
        defer { lock.unlock() }
        let result = SecItemDelete([ kSecClass : kSecClassGenericPassword] as CFDictionary)
        if result != noErr && result != errSecItemNotFound {
            fatalError("Cannot write to Keychain")
        }
    }
}

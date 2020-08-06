//Stub file made with Khrysalis 2 (by Lightning Kite)
import Foundation
import KeychainAccess

//--- SecurePreferences.{
public enum SecurePreferences {
    
    static public let INSTANCE = Self.self
    
    public static var keychain = Keychain(service: (Bundle.main.bundleIdentifier ?? "com.lightningkite.khrysalis.unknownApp") + ".securePreferences")
    
    public static func setKeychainAccessGroup(_ name: String) {
        print("Access name is now " + name)
        keychain = Keychain(accessGroup: name)
    }
    
    //--- SecurePreferences.sharedPreferences
    
    //--- SecurePreferences.set(String, T)
    public static func set<T: Codable>(_ key: String, _ value: T) { set(key: key, value: value) }
    public static func set<T: Codable>(key: String, value: T) {
        if let string = try? value.toJsonString() {
            keychain[key] = string
        }
    }
    
    //--- SecurePreferences.remove(String)
    
    public static func remove(_ key: String) -> Void { remove(key: key) }
    public static func remove(key: String) -> Void {
        try? keychain.remove(key)
    }
    
    //--- SecurePreferences.get(String)
    public static func get<T: Codable>(_ key: String) -> T? { return get(key: key) }
    public static func get<T: Codable>(key: String) -> T? {
        if let string = keychain[key],
            let parsed: T? = try? string.fromJsonString() {
            return parsed
        }
        return nil
    }
    
    //--- SecurePreferences.clear()
    public static func clear() -> Void {
        for key in keychain.allKeys() {
            remove(key)
        }
    }
    
    //--- SecurePreferences.}
}

//Stub file made with Khrysalis 2 (by Lightning Kite)
import Foundation


//--- Preferences.{
public enum Preferences {
    static public let INSTANCE = Self.self
    
    //--- Preferences.sharedPreferences
    
    //--- Preferences.set(String, T)
    public static func set<T: Codable>(_ key: String, _ value: T) { set(key: key, value: value) }
    public static func set<T: Codable>(key: String, value: T) {
        if let string = try? value.toJsonString() {
            UserDefaults.standard.setValue(string, forKey: key)
        }
    }
    
    //--- Preferences.remove(String)
    public static func remove(_ key: String) -> Void {
        UserDefaults.standard.removeObject(forKey: key)
    }
    public static func remove(key: String) -> Void {
        return remove(key)
    }
    
    //--- Preferences.get(String)
    public static func get<T: Codable>(_ key: String) -> T? { return get(key: key) }
    public static func get<T: Codable>(key: String) -> T? {
        if let string = UserDefaults.standard.string(forKey: key),
            let parsed: T? = try? string.fromJsonString() {
            return parsed
        }
        return nil
    }
    
    //--- Preferences.clear()
    public static func clear() -> Void {
        for k in UserDefaults.standard.dictionaryRepresentation().keys {
            UserDefaults.standard.removeObject(forKey: k)
        }
    }
    
    //--- Preferences.}
}

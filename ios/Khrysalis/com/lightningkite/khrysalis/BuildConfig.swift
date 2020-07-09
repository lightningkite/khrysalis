//Package: com.lightningkite.khrysalis
//Converted using Khrysalis2

import UIKit

public enum BuildConfig {
    static public var VERSION_NAME: String {
        return Bundle.main.infoDictionary?["CFBundleShortVersionString"] as? String ?? "Unknown Version"
    }
    static public var VERSION_CODE: Int {
        if let str = Bundle.main.infoDictionary?["CFBundleVersion"] as? String, let num = Int(str) {
            return num
        }
        return 0
    }
    static public var DEBUG: Bool {
        #if DEBUG
        return true
        #else
        return false
        #endif
    }
}

//Package: com.lightningkite.kwift
//Converted using Kwift2

import UIKit

public enum BuildConfig {
    static public var VERSION_NAME: String {
        return Bundle.main.infoDictionary?["CFBundleShortVersionString"] as? String ?? "Unknown Version"
    }
    static public var VERSION_CODE: Int32 {
        return (Bundle.main.infoDictionary?["CFBundleVersion"] as? String)?.toIntOrNull() ?? 0
    }
    static public var DEBUG: Bool {
        #if DEBUG
        return true
        #else
        return false
        #endif
    }
}

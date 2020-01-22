//Package: com.lightningkite.kwift
//Converted using Kwift2

import UIKit

public enum BuildConfig {
    var VERSION_NAME: String {
        return Bundle.main.infoDictionary?["CFBundleShortVersionString"] as? String ?? "Unknown Version"
    }
    var VERSION_CODE: Int {
        return (NSBundle.mainBundle().infoDictionary?["CFBundleVersion"] as? String)?.toIntOrNull() ?? 0
    }
    var DEBUG: Bool {
        #if DEBUG
        return true
        #else
        return false
        #endif
    }
}

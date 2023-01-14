import Foundation

public enum Platform {
    case Android
    case Ios
    case Web

    public class Companion {
        public static let INSTANCE = Companion()
        public let current: Platform = .Ios
    }
}

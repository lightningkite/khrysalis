import Foundation

public extension UUID {

    static func fromString(_ string: String) -> UUID {
        return UUID(uuidString: string)!
    }
    static func fromString(string: String) -> UUID {
        return fromString(string)
    }

    static func randomUUID() -> UUID {
        return UUID()
    }

}

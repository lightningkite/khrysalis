import Foundation

public protocol KEquatable: Equatable {
    func equals(other: Any) -> Bool
}
public extension KEquatable {
    static func ==(lhs: Self, rhs: Self) -> Bool {
        return lhs.equals(other: rhs)
    }
}
public protocol KHashable: Hashable {
    func hashCode() -> Int
}
public extension KHashable {
    func hash(into hasher: inout Hasher) {
        hasher.combine(hashCode())
    }
}
public protocol KStringable: CustomStringConvertible {
    func toString() -> String
}
public extension KStringable {
    var description: String {
        return toString()
    }
}
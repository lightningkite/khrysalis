import Foundation

public protocol KHashable: Hashable {
    func hashCode() -> Int32
}
public extension KHashable {
    func hash(into hasher: inout Hasher) {
        hasher.combine(hashCode())
    }
}
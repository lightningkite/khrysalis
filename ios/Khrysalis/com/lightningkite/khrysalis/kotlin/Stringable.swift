import Foundation

public protocol Stringable: CustomStringConvertible {
    func toString() -> String
}
public extension Stringable {
    var description: String {
        return toString()
    }
}

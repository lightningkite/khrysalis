import Foundation

public protocol Stringable: CustomStringConvertable {
    func toString() -> String
}
public extension Stringable {
    var description: String {
        return toString()
    }
}
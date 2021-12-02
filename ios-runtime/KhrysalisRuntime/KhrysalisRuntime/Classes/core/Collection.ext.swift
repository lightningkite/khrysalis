
import Foundation


public extension Collection {
    func find(_ predicate: (Element) -> Bool) -> Element? {
        return first(where: predicate)
    }
    static func +(first: Self, second: Element) -> Array<Element> {
        return first + [second]
    }
    func joinToString(_ separator: String, _ transform: (Element)->String) -> String {
        return self.map(transform).joined(separator: separator)
    }
    func joinToString(separator: String = ", ", transform: (Element)->String) -> String {
        return self.map(transform).joined(separator: separator)
    }
    func count(predicate: (Element)->Bool) -> Int {
        var current: Int = 0
        for item in self {
            if predicate(item) {
                current += 1
            }
        }
        return current
    }
}

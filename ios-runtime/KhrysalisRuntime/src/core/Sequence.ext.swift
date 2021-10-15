//
//  Created by Joseph Ivie on 12/12/18.
//

import Foundation

public extension Sequence {
    func first() -> Element {
        for item in self {
            return item
        }
        fatalError("There is no first element, it's empty")
    }
    func firstOrNull() -> Element? {
        for item in self {
            return item
        }
        return nil
    }
    func last() -> Element {
        var current: Element? = nil
        for item in self {
            current = item
        }
        return current!
    }
    func lastOrNull() -> Element? {
        var current: Element? = nil
        for item in self {
            current = item
        }
        return current
    }
    func distinctBy<U: Hashable>(selector: (Iterator.Element)->U) -> [Iterator.Element] {
        var seen: [U: Bool] = [:]
        return self.filter { seen.updateValue(true, forKey: selector($0)) == nil }
    }
    func chunked(size: Int) -> Array<Array<Element>> {
        var output = Array<Array<Element>>()
        var current = Array<Element>()
        for item in self {
            current.append(item)
            if current.count >= size {
                output.append(current)
                current = Array()
            }
        }
        if !current.isEmpty {
            output.append(current)
        }
        return output
    }
    
    func joined(separator: String = ", ", transform: (Element)->String) -> String {
        return self.map(transform).joined(separator: separator)
    }
}

public extension Sequence where Iterator.Element: Hashable {
    func distinct() -> [Iterator.Element] {
        var seen: [Iterator.Element: Bool] = [:]
        return self.filter { seen.updateValue(true, forKey: $0) == nil }
    }
}

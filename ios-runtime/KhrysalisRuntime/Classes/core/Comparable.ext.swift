import Foundation

public func makeComparator<T>(function: @escaping (T, T)->Int) -> Comparator {
    return { (a: Any, b: Any) in
        if let a = a as? T {
            if let b = b as? T {
                let num = function(a, b)
                if num > 0 {
                    return .orderedDescending
                } else if num < 0 {
                    return .orderedAscending
                } else {
                    return .orderedSame
                }
            } else {
                return .orderedDescending
            }
        } else {
            return .orderedAscending
        }
    }
}

public func deferComparison<T, C: Comparable>(_ get: @escaping (T) -> C?) -> (T, T) -> Bool {
    return { (a, b) in
        guard let va = get(a) else { return true }
        guard let vb = get(b) else { return false }
        return va < vb
    }
}
public func deferComparisonDescending<T, C: Comparable>(_ get: @escaping (T) -> C?) -> (T, T) -> Bool {
    return { (a, b) in
        guard let va = get(a) else { return false }
        guard let vb = get(b) else { return true }
        return va > vb
    }
}

public extension Comparable {
    func compareTo(other: Self) -> Int {
        return compareTo(other)
    }
    func compareTo(_ other: Self) -> Int {
        if self > other {
            return ComparisonResult.orderedDescending.rawValue
        } else if self == other {
            return ComparisonResult.orderedSame.rawValue
        } else {
            return ComparisonResult.orderedAscending.rawValue
        }
    }
}

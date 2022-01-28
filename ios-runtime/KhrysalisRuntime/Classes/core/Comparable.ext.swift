import Foundation

public typealias TypedComparator<T> = (T, T)->ComparisonResult

public func makeComparator<T>(function: @escaping (T, T)->Int) -> TypedComparator<T> {
    return { (a: T, b: T) in
        let num = function(a, b)
        if num > 0 {
            return .orderedDescending
        } else if num < 0 {
            return .orderedAscending
        } else {
            return .orderedSame
        }
    }
}

public func compareBy<T, C: Comparable>(selector: @escaping (T) -> C?) -> TypedComparator<T> {
    return { (a, b) in
        guard let va = selector(a) else { return .orderedDescending }
        guard let vb = selector(b) else { return .orderedAscending }
        return va.compareToResult(vb)
    }
}
public func compareByDescending<T, C: Comparable>(selector: @escaping (T) -> C?) -> TypedComparator<T> {
    return { (a, b) in
        guard let va = selector(a) else { return .orderedAscending }
        guard let vb = selector(b) else { return .orderedDescending }
        return -va.compareToResult(vb)
    }
}
public func compareBy<T, C: Comparable>(selector: @escaping (T) -> C) -> TypedComparator<T> {
    return { (a, b) in
        return selector(a).compareToResult(selector(b))
    }
}
public func compareByDescending<T, C: Comparable>(selector: @escaping (T) -> C) -> TypedComparator<T> {
    return { (a, b) in
        return -selector(a).compareToResult(selector(b))
    }
}

public extension Comparable {
    func compareTo(other: Self) -> Int {
        return compareTo(other)
    }
    func compareTo(_ other: Self) -> Int { compareToResult(other).rawValue }
    func compareToResult(_ other: Self) -> ComparisonResult {
        if self > other {
            return ComparisonResult.orderedDescending
        } else if self == other {
            return ComparisonResult.orderedSame
        } else {
            return ComparisonResult.orderedAscending
        }
    }
}

public extension ComparisonResult {
    static prefix func -(value: Self) -> Self {
        switch value {
        case .orderedAscending:
            return .orderedDescending
        case .orderedSame:
            return .orderedSame
        case .orderedDescending:
            return .orderedAscending
        }
    }
}

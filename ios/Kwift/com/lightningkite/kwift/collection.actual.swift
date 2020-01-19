//Stub file made with Kwift 2 (by Lightning Kite)
import Foundation


//--- List<T>.withoutIndex(Int)

public extension Array {

    //--- Iterable<T>.sumByLong((T)->Long)
    func sumByLong(selector: (Element) -> Int64)-> Int64{
        var sum:Int64 = 0
        for item in self {
            sum += selector(item)
        }
        return sum
    }

    //--- MutableList<T>.binaryInsertBy(T, (T)->K?)
    mutating func binaryInsertBy<K: Comparable>(
        item: Element,
        selector: (Element)->K?
    ) {
        let index = binarySearchBy(selector(item), selector)
        if index < 0 {
            add(
                -index - 1,
                item
            )
        } else {
            add(
                index,
                item
            )
        }
    }
    mutating func binaryInsertBy<K: Comparable>(
        _ item: Element,
        _ selector: (Element)->K?
    ) {
        binaryInsertBy(item: item, selector: selector)
    }

    //--- List<T>.binaryFind(K, (T)->K?)
    func binaryFind<K: Comparable>(_ key: K, _ selector: (Element) -> K?) -> Element?  {
        let index = binarySearchBy(key, selector)
        if index >= 0 {
            return self[index]
        } else {
            return nil
        }
    }

    //--- List<T>.binaryForEach(K, K, (T)->K?, (T)->Unit)
    func binaryForEach<K: Comparable>(_ lower: K, _ upper: K, _ selector: (Element) -> K?, _ action: (Element) -> Void) -> Void {
        var index = binarySearchBy(lower, selector)
        if index < 0 {
            index = -index - 1
        }
        while index < size {
            let item = self[index]
            let itemK = selector(item)
            if let itemK = itemK, itemK > upper { break }
            action(item)
            index += 1
        }
    }
    func binaryForEach<K: Comparable>(lower: K, upper: K, selector: (Element) -> K?, action: (Element) -> Void) -> Void {
        return binaryForEach(lower, upper, selector, action)
    }
    
    //--- List<T>.binarySearchBy(K?, (T)->K?)
    func binarySearchBy<K: Comparable>(
        _ key: K?,
        _ selector: (Element)->K?
    ) -> Int32 {
        binarySearchBy(key: key, selector: selector)
    }
    func binarySearchBy<K: Comparable>(
        key: K?,
        selector: (Element)->K?
    ) -> Int32 {
        let a = key
        return binarySearchBy(
            comparison: { item in
                guard let a = a else { return .orderedAscending }
                guard let b = selector(item) else { return .orderedDescending }
                return b.compare(to: a)
            }
        )
    }
    
    func binarySearchBy(
        comparison: (Element)->ComparisonResult
    ) -> Int32{
        var low = 0
        var high = count - 1
        while low <= high {
            let mid = (low + high) >> 1
            let cmp = comparison(self[mid])
            switch cmp {
            case .orderedAscending:
                low = mid + 1
            case .orderedSame:
                return Int32(mid)
            case .orderedDescending:
                high = mid - 1
            }
        }
        return Int32(-(low + 1))
    }
}

fileprivate extension Comparable {
    func compare(to other: Self) -> ComparisonResult {
        if self < other {
            return .orderedAscending
        } else if self > other {
            return .orderedDescending
        } else {
            return .orderedSame
        }
    }
}



























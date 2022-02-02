
import Foundation

public extension Array {

    func getOrNull(index: Int) -> Element? {
        if index >= count { return nil }
        return self[index]
    }

    func forEachIndexed(action: (_ index:Int, Element) -> Void){
        for index in 0..<self.count{
            action(Int(index), self[index])
        }
    }

    func plus(_ element: Element) -> Array<Element> {
        var copy = self
        copy.append(element)
        return copy
    }
    func withoutIndex(index: Int) -> Array<Element> {
        var copy = self
        copy.remove(at: index)
        return copy
    }
    func sumBy(selector: (Element) -> Int)-> Int{
        var sum:Int = 0
        for item in self{
            sum += selector(item)
        }
        return sum
    }
    func sumByDouble(selector: (Element) -> Double) -> Double{
        var sum:Double = 0.0
        for item in self{
            sum += selector(item)
        }
        return sum
    }
    
    func reduceOrNull(_ action: (Element, Element) -> Element) -> Element? {
        if self.isEmpty { return nil }
        var current = self[0]
        for i in 1..<self.endIndex {
            current = action(current, self[i])
        }
        return current
    }
}

public extension Array where Element: Equatable {
    mutating func remove(element: Element) {
        remove(element)
    }
    mutating func remove(_ element: Element) {
        let index = self.firstIndex(where: { sub in
            sub == element
        })
        if let index = index {
            remove(at: index)
        }
    }
    static func -(first: Array<Element>, second: Element) -> Array<Element> {
        var copy = first
        copy.remove(second)
        return copy
    }
    func minus(_ element: Element) -> Array<Element> {
        var copy = self
        copy.remove(element)
        return copy
    }
    func minus(element: Element) -> Array<Element> {
        return self.minus(element)
    }
    func minus(_ elements: Array<Element>) -> Array<Element> {
        var copy = self
        for element in elements {
            copy.remove(element)
        }
        return copy
    }
    func minus(elements: Array<Element>) -> Array<Element> {
        return self.minus(elements)
    }
}

//public extension Array where Element: AnyObject {
//    mutating func remove(element: Element) {
//        remove(element)
//    }
//    mutating func remove(_ element: Element) {
//        let index = self.firstIndex(where: { sub in
//            sub === element
//        })
//        if let index = index {
//            remove(at: index)
//        }
//    }
//    static func -(first: Array<Element>, second: Element) -> Array<Element> {
//        var copy = first
//        copy.remove(second)
//        return copy
//    }
//    func minus(_ element: Element) -> Array<Element> {
//        var copy = self
//        copy.remove(element)
//        return copy
//    }
//    func minus(element: Element) -> Array<Element> {
//        return self.minus(element)
//    }
//}

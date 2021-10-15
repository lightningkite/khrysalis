
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
    func sumByDouble(selector: (Element) -> Double)-> Double{
        var sum:Double = 0.0
        for item in self{
            sum += selector(item)
        }
        return sum
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
}

public extension Array where Element: AnyObject {
    mutating func remove(element: Element) {
        remove(element)
    }
    mutating func remove(_ element: Element) {
        let index = self.firstIndex(where: { sub in
            sub === element
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
}

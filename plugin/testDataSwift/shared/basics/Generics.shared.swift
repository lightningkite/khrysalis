//Package: com.test
//Converted using Khrysalis2

import Foundation



public class NameTag<T: Hashable>: Equatable, Hashable {
    
    public var item: T
    public var name: String
    
    public static func == (lhs: NameTag, rhs: NameTag) -> Bool {
        return lhs.item == rhs.item &&
            lhs.name == rhs.name
    }
    public func hash(into hasher: inout Hasher) {
        hasher.combine(item)
        hasher.combine(name)
    }
    public func copy(
        item: (T)? = nil,
        name: (String)? = nil
    ) -> NameTag {
        return NameTag(
            item: item ?? self.item,
            name: name ?? self.name
        )
    }
    
    
    public init(item: T, name: String) {
        self.item = item
        self.name = name
    }
    convenience public init(_ item: T, _ name: String) {
        self.init(item: item, name: name)
    }
}
 
 

extension NameTag where T: Hashable {
    public func printSelf() -> Void {
        print("Hello!  I am \(name).")
    }
}
 
 

extension NameTag where T == Int32 {
    public func printInt() -> Void {
        print("Hello!  I am \(name), I stand for \(item).")
    }
}
 
 

public func printAlt<T: Hashable>(nameTag: NameTag<T>) -> Void {
    print("Hello!  I am \(nameTag.name)")
}
public func printAlt<T: Hashable>(_ nameTag: NameTag<T>) -> Void {
    return printAlt(nameTag: nameTag)
}
 
 

public func main() -> Void {
    var tag = NameTag(2.toInt(), "Two")
    tag.printSelf()
    tag.printInt()
    printAlt(tag)
}
 

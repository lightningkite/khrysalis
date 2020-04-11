//Package: null
//Converted using Khrysalis2

import Foundation
import RxSwift
import RxRelay



public class Node {
    
    public var parentNode: Node?
    public var nodeName: String?
    
    
    public func getParent() -> Node?  {
        return parentNode
    }
    
    public func getName() -> String?  {
        return nodeName
    }
    
    public init(parentNode: Node?, nodeName: String?) {
        self.parentNode = parentNode
        self.nodeName = nodeName
    }
    convenience public init(_ parentNode: Node?, _ nodeName: String?) {
        self.init(parentNode: parentNode, nodeName: nodeName)
    }
}
 
 

public func foo(node: Node) -> String?  {
    var parent = node.getParent() ?? return nil
    var name = node.getName() ?? "THROW IS CURRENTLY NOT SUPPORTED"
    return "foo returns \(name)"
}
public func foo(_ node: Node) -> String?  {
    return foo(node: node)
}
 
 

public func main(args: Array<String>) -> Void {
    var b: String?  = "asdf"
    var c = {if let b = b {
        return b.length
    } else {
        return -1
    }}()
    var d = b?.length ?? -1
    var error = {if c != 4 || d != 4 {
        return "ERROR"
    } else {
        return "OK"
    }}()
    print("\(error): c=\(c) (should be 4), d=\(d) (should be 4)")
    var node1 = Node(parentNode: nil, nodeName: "node1Name")
    var node2 = Node(parentNode: node1, nodeName: "node2Name")
    print("\(node1): \(node1.getParent()) - \(node1.getName())")
    print("\(node2): \(node2.getParent()) - \(node2.getName())")
    if foo(node1) != nil {
        print("Error1")
    }
    if foo(node2) != "foo returns node2Name" {
        print("Error2")
    }
    var node3 = Node(parentNode: node2, nodeName: nil)
    print("\(node3): \(node3.getParent()) - \(node3.getName())")
    print(foo(node3))
}
public func main(_ args: Array<String>) -> Void {
    return main(args: args)
}
 

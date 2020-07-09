import Foundation

public protocol BoxProtocol {
    associatedtype T
    func asBox() -> Box<T>
}

//--- Box.{
public struct Box<T>: BoxProtocol {

    public func asBox() -> Box<T> {
        return self
    }

    //--- Box.Primary Constructor
    public init(_ value: T) {
        self.value = value
    }

    //--- Box.value
    public var value: T

    //--- Box.Companion.{

    //--- Box.Companion.wrap(T)
    public static func wrap(_ value: T) -> Box<T> {
        return Box(value)
    }
    public static func wrap(value: T) -> Box<T> {
        return Box(value)
    }

    //--- Box.Companion.}

    //--- Box.}
}

//--- boxWrap(T)
public func boxWrap<T>(_ value: T) -> Box<T> {
    return Box(value)
}
public func boxWrap<T>(value: T) -> Box<T> {
    return Box(value)
}

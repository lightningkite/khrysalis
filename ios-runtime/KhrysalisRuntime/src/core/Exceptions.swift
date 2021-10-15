import Foundation

open class Exception: LocalizedError, CustomStringConvertible {
    public let message: String?
    public let cause: Error?
    public init(_ message: String? = nil, _ cause: Error? = nil) {
        self.message = message
        self.cause = cause
    }

    public var description: String { return message ?? "\(Self.self)" }
    public var errorDescription: String? { return message }
}

public class IllegalStateException: Exception {}
public class IndexOutOfBoundsException: Exception {}
public class IllegalArgumentException: Exception {}
public class NoSuchElementException: Exception {}

public extension Error {
    func printStackTrace(){
        print(self.localizedDescription)
    }
}

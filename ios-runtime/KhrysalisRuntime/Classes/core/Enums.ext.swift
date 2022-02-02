//
//  Created by Joseph Ivie on 12/12/18.
//

import Foundation

public protocol KotlinEnum: CaseIterable, Equatable {
    static var caseNames: Array<String> { get }
}

public extension CaseIterable where Self: Equatable {
    var ordinal: Int { return Array(Self.allCases).firstIndex(of: self)! }
}

public extension KotlinEnum {
    /// A collection of all values of this type.
    static func values() -> Array<Self> {
        return Array(self.allCases)
    }
    static func valueOf(_ value: String) -> Self {
        if let index = Self.caseNames.firstIndex(of: value) {
            return Array(Self.allCases)[index]
        } else {
            return Self.allCases.first!
        }
    }
    var name: String { return Self.caseNames[self.ordinal] }

    init(from decoder: Decoder) throws {
        self = Self.valueOf(try decoder.singleValueContainer().decode(String.self))
    }

    func encode(to encoder: Encoder) throws {
        var svc = encoder.singleValueContainer()
        try svc.encode(name)
    }
}

public class WeakReference<T: AnyObject> {
    weak var item: T?
    public init(_ item: T) {
        self.item = item
    }
    public func get() -> T? {
        return item
    }
}



//
//  Created by Joseph Ivie on 12/12/18.
//

import Foundation

public extension CaseIterable {
    /// A collection of all values of this type.
    static func values() -> Array<Self> {
        return Array(self.allCases)
    }
    static func valueOf(_ value: String) -> Self {
        return values().find { "\($0)" == value }!
    }
}

public protocol StringEnum {
    var rawValue: String { get }
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



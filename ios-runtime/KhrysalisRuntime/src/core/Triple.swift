//
//  Created by Joseph Ivie on 12/12/18.
//

import Foundation

// Exists for Equatable / Hashable

public struct Triple<A, B, C> {
    public let first: A
    public let second: B
    public let third: C
    public init(first: A, second: B, third: C) {
        self.first = first
        self.second = second
        self.third = third
    }
    public init(_ first: A, _ second: B, _ third: C) {
        self.first = first
        self.second = second
        self.third = third
    }
    public func toTuple() -> (A, B, C) { return (first, second, third) }
}

extension Triple: Encodable where A: Encodable, B: Encodable, C: Encodable { }
extension Triple: Decodable where A: Decodable, B: Decodable, C: Decodable { }
extension Triple: Equatable where A: Equatable, B: Equatable, C: Equatable { }
extension Triple: Hashable where A: Hashable, B: Hashable, C: Hashable { }
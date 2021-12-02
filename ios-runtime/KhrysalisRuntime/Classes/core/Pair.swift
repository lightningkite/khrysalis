//
//  Created by Joseph Ivie on 12/12/18.
//

import Foundation

// Why make our own Pair class?  Because the tuples in Swift can't be equatable / hashable

public struct Pair<A, B> {
    public let first: A
    public let second: B
    public init(first: A, second: B) {
        self.first = first
        self.second = second
    }
    public init(_ first: A, _ second: B) {
        self.first = first
        self.second = second
    }
    public func toTuple() -> (A, B) { return (first, second) }
    
    public func copy( first:A? = nil, second:B? = nil ) -> Pair<A,B> { return Pair(first ?? self.first, second ?? self.second)}
}

extension Pair: Encodable where A: Encodable, B: Encodable { }
extension Pair: Decodable where A: Decodable, B: Decodable { }
extension Pair: Equatable where A: Equatable, B: Equatable { }
extension Pair: Hashable where A: Hashable, B: Hashable { }

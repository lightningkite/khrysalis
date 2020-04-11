//Package: com.lightningkite.khrysalis.test
//Converted using Khrysalis2

import Foundation
import RxSwift
import RxRelay



public class TestDataClass: Serializable, Equatable, Hashable {
    
    public var a: Double
    public var b: String
    public var c: Array<Int32>
    
    public static func == (lhs: TestDataClass, rhs: TestDataClass) -> Bool {
        return lhs.a == rhs.a &&
            lhs.b == rhs.b &&
            lhs.c == rhs.c
    }
    public func hash(into hasher: inout Hasher) {
        hasher.combine(a)
        hasher.combine(b)
        hasher.combine(c)
    }
    public func copy(
        a: (Double)? = nil,
        b: (String)? = nil,
        c: (Array<Int32>)? = nil
    ) -> TestDataClass {
        return TestDataClass(
            a: a ?? self.a,
            b: b ?? self.b,
            c: c ?? self.c
        )
    }
    
    
    public init(a: Double = 0.0, b: String = "Hello!", c: Array<Int32> = Array<Int32>) {
        self.a = a
        self.b = b
        self.c = c
    }
    convenience public init(_ a: Double, _ b: String = "Hello!", _ c: Array<Int32> = Array<Int32>) {
        self.init(a: a, b: b, c: c)
    }
}
 

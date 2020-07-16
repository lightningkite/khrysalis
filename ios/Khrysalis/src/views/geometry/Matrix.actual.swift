//
//  Matrix.actual.swift
//  Khrysalis
//
//  Created by Joseph Ivie on 7/16/20.
//  Copyright © 2020 Lightning Kite. All rights reserved.
//

import CoreGraphics

extension CGAffineTransform {
    public mutating func reset() {
        self = CGAffineTransform.identity
    }
    public mutating func set(_ other: CGAffineTransform) {
        self = other
    }
    public mutating func apply(_ f: (CGAffineTransform)->()->CGAffineTransform) {
        self = f(self)()
    }
    public mutating func apply<A>(_ f: (CGAffineTransform)->(A)->CGAffineTransform, _ a: A) {
        self = f(self)(a)
    }
    public mutating func apply<A, B>(_ f: (CGAffineTransform)->(A, B)->CGAffineTransform, _ a: A, _ b: B) {
        self = f(self)(a, b)
    }
    public mutating func apply<A, B, C>(_ f: (CGAffineTransform)->(A, B, C)->CGAffineTransform, _ a: A, _ b: B, _ c: C) {
        self = f(self)(a, b, c)
    }
    public mutating func apply<A, B, C, D>(_ f: (CGAffineTransform)->(A, B, C, D)->CGAffineTransform, _ a: A, _ b: B, _ c: C, _ d: D) {
        self = f(self)(a, b, c, d)
    }
}

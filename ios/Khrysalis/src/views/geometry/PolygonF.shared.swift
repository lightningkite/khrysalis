// Generated by Khrysalis Swift converter - this file will be overwritten.
// File: views/geometry/PolygonF.shared.kt
// Package: com.lightningkite.khrysalis.views.geometry
import Foundation
import CoreGraphics

public class PolygonF : KDataClass {
    public var points: Array<CGPoint>
    public init(points: Array<CGPoint>) {
        self.points = points
    }
    public func hash(into hasher: inout Hasher) {
        hasher.combine(points)
    }
    public static func == (lhs: PolygonF, rhs: PolygonF) -> Bool { return lhs.points == rhs.points }
    public var description: String { return "PolygonF(points = \(self.points))" }
    public func copy(points: Array<CGPoint>? = nil) -> PolygonF { return PolygonF(points: points ?? self.points) }
    
    public func contains(point: CGPoint) -> Bool {
        var inside = false
        let big: CGFloat = 1000
        for index in ((0..<self.points.count - 2)){
            var a = self.points[index]
            var b = self.points[index + 1]
            let denom = -(big - point.x) * (b.y - a.y)
            if denom == 0 { continue }
            let ua = ((big - point.x) * (a.y - point.y)) / denom
            let ub = ((b.x - a.x) * (a.y - point.y) - (b.y - a.y) * (a.x - point.x)) / denom
            if ua >= 0.0, ua <= 1.0, ub >= 0.0, ub <= 1.0 {
                inside = !inside
            }
        }
        return inside
    }
}


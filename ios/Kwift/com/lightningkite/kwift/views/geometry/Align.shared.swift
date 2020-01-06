//Package: com.lightningkite.kwift.views.geometry
//Converted using Kwift2

import Foundation



public enum Align: String, StringEnum, CaseIterable, Codable {
    case start = "start"
    case center = "center"
    case end = "end"
    case fill = "fill"
    public init(from decoder: Decoder) throws {
        self = try Align(rawValue: decoder.singleValueContainer().decode(RawValue.self)) ?? .start
    }
}
 
 

public class AlignPair: Equatable, Hashable {
    
    public var horizontal: Align
    public var vertical: Align
    
    public static func == (lhs: AlignPair, rhs: AlignPair) -> Bool {
        return lhs.horizontal == rhs.horizontal &&
            lhs.vertical == rhs.vertical
    }
    public func hash(into hasher: inout Hasher) {
        hasher.combine(horizontal)
        hasher.combine(vertical)
    }
    public func copy(
        horizontal: (Align)? = nil,
        vertical: (Align)? = nil
    ) -> AlignPair {
        return AlignPair(
            horizontal: horizontal ?? self.horizontal,
            vertical: vertical ?? self.vertical
        )
    }
    
    
    //Start Companion
    static public var center = AlignPair(horizontal: Align.center, vertical: Align.center)
    static public var fill = AlignPair(horizontal: Align.fill, vertical: Align.fill)
    static public var topLeft = AlignPair(horizontal: Align.start, vertical: Align.start)
    static public var topCenter = AlignPair(horizontal: Align.center, vertical: Align.start)
    static public var topFill = AlignPair(horizontal: Align.fill, vertical: Align.start)
    static public var topRight = AlignPair(horizontal: Align.end, vertical: Align.start)
    static public var centerLeft = AlignPair(horizontal: Align.start, vertical: Align.center)
    static public var centerCenter = AlignPair(horizontal: Align.center, vertical: Align.center)
    static public var centerFill = AlignPair(horizontal: Align.fill, vertical: Align.center)
    static public var centerRight = AlignPair(horizontal: Align.end, vertical: Align.center)
    static public var fillLeft = AlignPair(horizontal: Align.start, vertical: Align.fill)
    static public var fillCenter = AlignPair(horizontal: Align.center, vertical: Align.fill)
    static public var fillFill = AlignPair(horizontal: Align.fill, vertical: Align.fill)
    static public var fillRight = AlignPair(horizontal: Align.end, vertical: Align.fill)
    static public var bottomLeft = AlignPair(horizontal: Align.start, vertical: Align.end)
    static public var bottomCenter = AlignPair(horizontal: Align.center, vertical: Align.end)
    static public var bottomFill = AlignPair(horizontal: Align.fill, vertical: Align.end)
    static public var bottomRight = AlignPair(horizontal: Align.end, vertical: Align.end)
    //End Companion
    
    
    public init(horizontal: Align, vertical: Align) {
        self.horizontal = horizontal
        self.vertical = vertical
    }
    convenience public init(_ horizontal: Align, _ vertical: Align) {
        self.init(horizontal: horizontal, vertical: vertical)
    }
}
 

//
//  Layouting.swift
//  Kwift
//
//  Created by Joseph Ivie on 10/12/19.
//  Copyright © 2019 Lightning Kite. All rights reserved.
//

import UIKit

public enum Align {
    case start, center, end, fill
}

public struct AlignPair {
    public let horizontal: Align
    public let vertical: Align
    
    public init(horizontal: Align, vertical: Align) {
        self.horizontal = horizontal
        self.vertical = vertical
    }
    
    static public let center = AlignPair(horizontal: .center, vertical: .center)
    static public let fill = AlignPair(horizontal: .fill, vertical: .fill)
    
    static public let topLeft = AlignPair(horizontal: .start, vertical: .start)
    static public let topCenter = AlignPair(horizontal: .center, vertical: .start)
    static public let topFill = AlignPair(horizontal: .fill, vertical: .start)
    static public let topRight = AlignPair(horizontal: .end, vertical: .start)
    static public let centerLeft = AlignPair(horizontal: .start, vertical: .center)
    static public let centerCenter = AlignPair(horizontal: .center, vertical: .center)
    static public let centerFill = AlignPair(horizontal: .fill, vertical: .center)
    static public let centerRight = AlignPair(horizontal: .end, vertical: .center)
    static public let fillLeft = AlignPair(horizontal: .start, vertical: .fill)
    static public let fillCenter = AlignPair(horizontal: .center, vertical: .fill)
    static public let fillFill = AlignPair(horizontal: .fill, vertical: .fill)
    static public let fillRight = AlignPair(horizontal: .end, vertical: .fill)
    static public let bottomLeft = AlignPair(horizontal: .start, vertical: .end)
    static public let bottomCenter = AlignPair(horizontal: .center, vertical: .end)
    static public let bottomFill = AlignPair(horizontal: .fill, vertical: .end)
    static public let bottomRight = AlignPair(horizontal: .end, vertical: .end)
    
}

public enum Dimension {
    case x, y
}

public extension Dimension {
    var other: Dimension {
        switch self {
        case .x:
            return .y
        case .y:
            return .x
        }
    }
}

public extension AlignPair {
    subscript(dimension: Dimension) -> Align {
        get {
            switch dimension {
            case .x:
                return self.horizontal
            case .y:
                return self.vertical
            }
        }
    }
}

public extension CGSize {
    subscript(dimension: Dimension) -> CGFloat {
        get {
            switch dimension {
            case .x:
                return self.width
            case .y:
                return self.height
            }
        }
        set(value) {
            switch dimension {
            case .x:
                self.width = value
            case .y:
                self.height = value
            }
        }
    }
}

public extension CGPoint {
    subscript(dimension: Dimension) -> CGFloat {
        get {
            switch dimension {
            case .x:
                return self.x
            case .y:
                return self.y
            }
        }
        set(value) {
            switch dimension {
            case .x:
                self.x = value
            case .y:
                self.y = value
            }
        }
    }
}

public extension UIEdgeInsets {
    func start(_ dimension: Dimension) -> CGFloat {
        switch dimension {
        case .x:
            return self.left
        case .y:
            return self.top
        }
    }
    func end(_ dimension: Dimension) -> CGFloat {
        switch dimension {
        case .x:
            return self.right
        case .y:
            return self.bottom
        }
    }
    func total(_ dimension: Dimension) -> CGFloat {
        switch dimension {
        case .x:
            return self.left + self.right
        case .y:
            return self.top + self.bottom
        }
    }
}

//
//  Layouting.swift
//  Khrysalis
//
//  Created by Joseph Ivie on 10/12/19.
//  Copyright © 2019 Lightning Kite. All rights reserved.
//

import UIKit

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
    func expand(_ rhs: CGSize) -> CGSize {
        return CGSize(width: max(self.width, rhs.width), height: max(self.height, rhs.height))
    }

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

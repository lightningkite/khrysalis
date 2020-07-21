import Foundation

public extension AlignPair {
    convenience init(_ horizontal: Align, _ vertical: Align) {
        self.init(horizontal: horizontal, vertical: vertical)
    }

    static var center: AlignPair { return AlignPair.Companion.INSTANCE.center }
    static var fill: AlignPair { return AlignPair.Companion.INSTANCE.fill }

    static var topLeft: AlignPair { return AlignPair.Companion.INSTANCE.topLeft }
    static var topCenter: AlignPair { return AlignPair.Companion.INSTANCE.topCenter }
    static var topFill: AlignPair { return AlignPair.Companion.INSTANCE.topFill }
    static var topRight: AlignPair { return AlignPair.Companion.INSTANCE.topRight }
    static var centerLeft: AlignPair { return AlignPair.Companion.INSTANCE.centerLeft }
    static var centerCenter: AlignPair { return AlignPair.Companion.INSTANCE.centerCenter }
    static var centerFill: AlignPair { return AlignPair.Companion.INSTANCE.centerFill }
    static var centerRight: AlignPair { return AlignPair.Companion.INSTANCE.centerRight }
    static var fillLeft: AlignPair { return AlignPair.Companion.INSTANCE.fillLeft }
    static var fillCenter: AlignPair { return AlignPair.Companion.INSTANCE.fillCenter }
    static var fillFill: AlignPair { return AlignPair.Companion.INSTANCE.fillFill }
    static var fillRight: AlignPair { return AlignPair.Companion.INSTANCE.fillRight }
    static var bottomLeft: AlignPair { return AlignPair.Companion.INSTANCE.bottomLeft }
    static var bottomCenter: AlignPair { return AlignPair.Companion.INSTANCE.bottomCenter }
    static var bottomFill: AlignPair { return AlignPair.Companion.INSTANCE.bottomFill }
    static var bottomRight: AlignPair { return AlignPair.Companion.INSTANCE.bottomRight }
}
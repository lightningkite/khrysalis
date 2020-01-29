//Package: com.lightningkite.khrysalis.bluetooth
//Converted using Khrysalis2

import Foundation
import RxSwift
import RxRelay
import Khrysalis



public class BleDeviceInfo: Equatable, Hashable {
    
    public var id: String
    public var name: String?

    
    public static func == (lhs: BleDeviceInfo, rhs: BleDeviceInfo) -> Bool {
        return lhs.id == rhs.id &&
            lhs.name == rhs.name
    }
    public func hash(into hasher: inout Hasher) {
        hasher.combine(id)
        hasher.combine(name)
    }
    public func copy(
        id: (String)? = nil,
        name: (String?
)? = nil
    ) -> BleDeviceInfo {
        return BleDeviceInfo(
            id: id ?? self.id,
            name: name ?? self.name
        )
    }
    
    
    public init(id: String, name: String?
) {
        self.id = id
        self.name = name
    }
    convenience public init(_ id: String, _ name: String?
) {
        self.init(id: id, name: name)
    }
}
 

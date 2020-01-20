//Package: com.lightningkite.kwift.bluetooth
//Converted using Kwift2

import Foundation
import RxSwift
import RxRelay



public class BleDeviceInfo: Equatable, Hashable {
    
    public var id: String
    public var name: String
    public var rssi: Int32
    
    public static func == (lhs: BleDeviceInfo, rhs: BleDeviceInfo) -> Bool {
        return lhs.id == rhs.id &&
            lhs.name == rhs.name &&
            lhs.rssi == rhs.rssi
    }
    public func hash(into hasher: inout Hasher) {
        hasher.combine(id)
        hasher.combine(name)
        hasher.combine(rssi)
    }
    public func copy(
        id: (String)? = nil,
        name: (String)? = nil,
        rssi: (Int32)? = nil
    ) -> BleDeviceInfo {
        return BleDeviceInfo(
            id: id ?? self.id,
            name: name ?? self.name,
            rssi: rssi ?? self.rssi
        )
    }
    
    
    public init(id: String, name: String, rssi: Int32) {
        self.id = id
        self.name = name
        self.rssi = rssi
    }
    convenience public init(_ id: String, _ name: String, _ rssi: Int32) {
        self.init(id: id, name: name, rssi: rssi)
    }
}
 

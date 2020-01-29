//Package: com.lightningkite.khrysalis.bluetooth
//Converted using Khrysalis2

import Foundation
import RxSwift
import RxRelay
import Khrysalis



public class BleScanResult: Equatable, Hashable {
    
    public var info: BleDeviceInfo
    public var rssi: Int32
    
    public static func == (lhs: BleScanResult, rhs: BleScanResult) -> Bool {
        return lhs.info == rhs.info &&
            lhs.rssi == rhs.rssi
    }
    public func hash(into hasher: inout Hasher) {
        hasher.combine(info)
        hasher.combine(rssi)
    }
    public func copy(
        info: (BleDeviceInfo)? = nil,
        rssi: (Int32)? = nil
    ) -> BleScanResult {
        return BleScanResult(
            info: info ?? self.info,
            rssi: rssi ?? self.rssi
        )
    }
    
    
    public init(info: BleDeviceInfo, rssi: Int32) {
        self.info = info
        self.rssi = rssi
    }
    convenience public init(_ info: BleDeviceInfo, _ rssi: Int32) {
        self.init(info: info, rssi: rssi)
    }
}
 

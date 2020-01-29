//Package: com.lightningkite.khrysalis.bluetooth
//Converted using Khrysalis2

import Foundation
import RxSwift
import RxRelay
import Khrysalis



public class BleService: Equatable, Hashable {
    
    public var serviceUuid: UUID
    
    public static func == (lhs: BleService, rhs: BleService) -> Bool {
        return lhs.serviceUuid == rhs.serviceUuid
    }
    public func hash(into hasher: inout Hasher) {
        hasher.combine(serviceUuid)
    }
    public func copy(
        serviceUuid: (UUID)? = nil
    ) -> BleService {
        return BleService(
            serviceUuid: serviceUuid ?? self.serviceUuid
        )
    }
    
    
    public init(serviceUuid: UUID) {
        self.serviceUuid = serviceUuid
    }
    convenience public init(_ serviceUuid: UUID) {
        self.init(serviceUuid: serviceUuid)
    }
}
 

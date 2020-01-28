//Package: com.lightningkite.kwift.bluetooth
//Converted using Kwift2

import Foundation
import RxSwift
import RxRelay
import Kwift



public class BleCharacteristic: Equatable, Hashable {
    
    public var serviceUuid: UUID
    public var characteristicUuid: UUID
    
    public static func == (lhs: BleCharacteristic, rhs: BleCharacteristic) -> Bool {
        return lhs.serviceUuid == rhs.serviceUuid &&
            lhs.characteristicUuid == rhs.characteristicUuid
    }
    public func hash(into hasher: inout Hasher) {
        hasher.combine(serviceUuid)
        hasher.combine(characteristicUuid)
    }
    public func copy(
        serviceUuid: (UUID)? = nil,
        characteristicUuid: (UUID)? = nil
    ) -> BleCharacteristic {
        return BleCharacteristic(
            serviceUuid: serviceUuid ?? self.serviceUuid,
            characteristicUuid: characteristicUuid ?? self.characteristicUuid
        )
    }
    
    
    public init(serviceUuid: UUID, characteristicUuid: UUID) {
        self.serviceUuid = serviceUuid
        self.characteristicUuid = characteristicUuid
    }
    convenience public init(_ serviceUuid: UUID, _ characteristicUuid: UUID) {
        self.init(serviceUuid: serviceUuid, characteristicUuid: characteristicUuid)
    }
}
 

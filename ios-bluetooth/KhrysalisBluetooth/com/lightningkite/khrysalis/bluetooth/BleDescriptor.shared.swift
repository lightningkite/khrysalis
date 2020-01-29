//Package: com.lightningkite.khrysalis.bluetooth
//Converted using Khrysalis2

import Foundation
import RxSwift
import RxRelay
import Khrysalis



public class BleDescriptor: Equatable, Hashable {
    
    public var serviceUuid: UUID
    public var characteristicUuid: UUID
    public var descriptorUuid: UUID
    
    public static func == (lhs: BleDescriptor, rhs: BleDescriptor) -> Bool {
        return lhs.serviceUuid == rhs.serviceUuid &&
            lhs.characteristicUuid == rhs.characteristicUuid &&
            lhs.descriptorUuid == rhs.descriptorUuid
    }
    public func hash(into hasher: inout Hasher) {
        hasher.combine(serviceUuid)
        hasher.combine(characteristicUuid)
        hasher.combine(descriptorUuid)
    }
    public func copy(
        serviceUuid: (UUID)? = nil,
        characteristicUuid: (UUID)? = nil,
        descriptorUuid: (UUID)? = nil
    ) -> BleDescriptor {
        return BleDescriptor(
            serviceUuid: serviceUuid ?? self.serviceUuid,
            characteristicUuid: characteristicUuid ?? self.characteristicUuid,
            descriptorUuid: descriptorUuid ?? self.descriptorUuid
        )
    }
    
    
    public init(serviceUuid: UUID, characteristicUuid: UUID, descriptorUuid: UUID) {
        self.serviceUuid = serviceUuid
        self.characteristicUuid = characteristicUuid
        self.descriptorUuid = descriptorUuid
    }
    convenience public init(_ serviceUuid: UUID, _ characteristicUuid: UUID, _ descriptorUuid: UUID) {
        self.init(serviceUuid: serviceUuid, characteristicUuid: characteristicUuid, descriptorUuid: descriptorUuid)
    }
}
 

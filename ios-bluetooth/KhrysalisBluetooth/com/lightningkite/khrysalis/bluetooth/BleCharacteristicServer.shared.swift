//Package: com.lightningkite.khrysalis.bluetooth
//Converted using Khrysalis2

import Foundation
import RxSwift
import RxRelay
import Khrysalis



public class BleCharacteristicProperties: Equatable, Hashable {
    
    public var broadcast: Bool
    public var read: Bool
    public var writeWithoutResponse: Bool
    public var write: Bool
    public var notify: Bool
    public var indicate: Bool
    public var authenticatedSignedWrites: Bool
    public var extendedProperties: Bool
    public var notifyEncryptionRequired: Bool
    public var indicateEncryptionRequired: Bool
    public var writeEncryptionRequired: Bool
    
    public static func == (lhs: BleCharacteristicProperties, rhs: BleCharacteristicProperties) -> Bool {
        return lhs.broadcast == rhs.broadcast &&
            lhs.read == rhs.read &&
            lhs.writeWithoutResponse == rhs.writeWithoutResponse &&
            lhs.write == rhs.write &&
            lhs.notify == rhs.notify &&
            lhs.indicate == rhs.indicate &&
            lhs.authenticatedSignedWrites == rhs.authenticatedSignedWrites &&
            lhs.extendedProperties == rhs.extendedProperties &&
            lhs.notifyEncryptionRequired == rhs.notifyEncryptionRequired &&
            lhs.indicateEncryptionRequired == rhs.indicateEncryptionRequired &&
            lhs.writeEncryptionRequired == rhs.writeEncryptionRequired
    }
    public func hash(into hasher: inout Hasher) {
        hasher.combine(broadcast)
        hasher.combine(read)
        hasher.combine(writeWithoutResponse)
        hasher.combine(write)
        hasher.combine(notify)
        hasher.combine(indicate)
        hasher.combine(authenticatedSignedWrites)
        hasher.combine(extendedProperties)
        hasher.combine(notifyEncryptionRequired)
        hasher.combine(indicateEncryptionRequired)
        hasher.combine(writeEncryptionRequired)
    }
    public func copy(
        broadcast: (Bool)? = nil,
        read: (Bool)? = nil,
        writeWithoutResponse: (Bool)? = nil,
        write: (Bool)? = nil,
        notify: (Bool)? = nil,
        indicate: (Bool)? = nil,
        authenticatedSignedWrites: (Bool)? = nil,
        extendedProperties: (Bool)? = nil,
        notifyEncryptionRequired: (Bool)? = nil,
        indicateEncryptionRequired: (Bool)? = nil,
        writeEncryptionRequired: (Bool)? = nil
    ) -> BleCharacteristicProperties {
        return BleCharacteristicProperties(
            broadcast: broadcast ?? self.broadcast,
            read: read ?? self.read,
            writeWithoutResponse: writeWithoutResponse ?? self.writeWithoutResponse,
            write: write ?? self.write,
            notify: notify ?? self.notify,
            indicate: indicate ?? self.indicate,
            authenticatedSignedWrites: authenticatedSignedWrites ?? self.authenticatedSignedWrites,
            extendedProperties: extendedProperties ?? self.extendedProperties,
            notifyEncryptionRequired: notifyEncryptionRequired ?? self.notifyEncryptionRequired,
            indicateEncryptionRequired: indicateEncryptionRequired ?? self.indicateEncryptionRequired,
            writeEncryptionRequired: writeEncryptionRequired ?? self.writeEncryptionRequired
        )
    }
    
    
    public init(broadcast: Bool = false, read: Bool = false, writeWithoutResponse: Bool = false, write: Bool = false, notify: Bool = false, indicate: Bool = false, authenticatedSignedWrites: Bool = false, extendedProperties: Bool = false, notifyEncryptionRequired: Bool = false, indicateEncryptionRequired: Bool = false, writeEncryptionRequired: Bool = false) {
        self.broadcast = broadcast
        self.read = read
        self.writeWithoutResponse = writeWithoutResponse
        self.write = write
        self.notify = notify
        self.indicate = indicate
        self.authenticatedSignedWrites = authenticatedSignedWrites
        self.extendedProperties = extendedProperties
        self.notifyEncryptionRequired = notifyEncryptionRequired
        self.indicateEncryptionRequired = indicateEncryptionRequired
        self.writeEncryptionRequired = writeEncryptionRequired
    }
    convenience public init(_ broadcast: Bool, _ read: Bool = false, _ writeWithoutResponse: Bool = false, _ write: Bool = false, _ notify: Bool = false, _ indicate: Bool = false, _ authenticatedSignedWrites: Bool = false, _ extendedProperties: Bool = false, _ notifyEncryptionRequired: Bool = false, _ indicateEncryptionRequired: Bool = false, _ writeEncryptionRequired: Bool = false) {
        self.init(broadcast: broadcast, read: read, writeWithoutResponse: writeWithoutResponse, write: write, notify: notify, indicate: indicate, authenticatedSignedWrites: authenticatedSignedWrites, extendedProperties: extendedProperties, notifyEncryptionRequired: notifyEncryptionRequired, indicateEncryptionRequired: indicateEncryptionRequired, writeEncryptionRequired: writeEncryptionRequired)
    }
}
 
 

public protocol BleCharacteristicServer {
    
    var characteristic: BleCharacteristic { get }
    
    var properties: BleCharacteristicProperties { get }
    
    func onSubscribe(from: BleClient) -> Void
    func onSubscribe(_ from: BleClient) -> Void
    
    func onUnsubscribe(from: BleClient) -> Void
    func onUnsubscribe(_ from: BleClient) -> Void
    
    func onDisconnect(from: BleClient) -> Void
    func onDisconnect(_ from: BleClient) -> Void
    
    func onRead(from: BleClient, request: RequestId) -> Void
    func onRead(_ from: BleClient, _ request: RequestId) -> Void
    
    func onWrite(from: BleClient, request: RequestId, value: Data) -> Void
    func onWrite(_ from: BleClient, _ request: RequestId, _ value: Data) -> Void
    
    func notify(client: BleClient, value: Data) -> Void
    func notify(_ client: BleClient, _ value: Data) -> Void
    
    func indicate(client: BleClient, value: Data) -> Void
    func indicate(_ client: BleClient, _ value: Data) -> Void
}

public extension BleCharacteristicServer {
    
    func notify(client: BleClient, value: Data) -> Void {
        return client.notify(characteristic.serviceUuid, characteristic.characteristicUuid, value)
    }
    func notify(_ client: BleClient, _ value: Data) -> Void {
        return notify(client: client, value: value)
    }
    
    func indicate(client: BleClient, value: Data) -> Void {
        return client.indicate(characteristic.serviceUuid, characteristic.characteristicUuid, value)
    }
    func indicate(_ client: BleClient, _ value: Data) -> Void {
        return indicate(client: client, value: value)
    }
}
 

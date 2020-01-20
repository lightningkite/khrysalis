//Package: com.lightningkite.kwift.bluetooth
//Converted using Kwift2

import Foundation
import RxSwift
import RxRelay



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
    
    var serviceUuid: UUID { get }
    
    var uuid: UUID { get }
    
    var properties: BleCharacteristicProperties { get }
    
    func onSubscribe(from: BleClient) -> Void
    func onSubscribe(_ from: BleClient) -> Void
    
    func onUnsubscribe(from: BleClient) -> Void
    func onUnsubscribe(_ from: BleClient) -> Void
    
    func onRead(from: BleClient, request: RequestId) -> Void
    func onRead(_ from: BleClient, _ request: RequestId) -> Void
    
    func onWrite(from: BleClient, request: RequestId, value: ByteArray) -> Void
    func onWrite(_ from: BleClient, _ request: RequestId, _ value: ByteArray) -> Void
    
    func notify(client: BleClient, value: ByteArray) -> Void
    func notify(_ client: BleClient, _ value: ByteArray) -> Void
    
    func indicate(client: BleClient, value: ByteArray) -> Void
    func indicate(_ client: BleClient, _ value: ByteArray) -> Void
}

public extension BleCharacteristicServer {
    
    func notify(client: BleClient, value: ByteArray) -> Void {
        return client.notify(serviceUuid, uuid, value)
    }
    func notify(_ client: BleClient, _ value: ByteArray) -> Void {
        return notify(client: client, value: value)
    }
    
    func indicate(client: BleClient, value: ByteArray) -> Void {
        return client.indicate(serviceUuid, uuid, value)
    }
    func indicate(_ client: BleClient, _ value: ByteArray) -> Void {
        return indicate(client: client, value: value)
    }
}
 
 

public class PropertyBleCharacteristicServer: MutableObservableProperty<ByteArray>, BleCharacteristicServer {
    
    public var serviceUuid: UUID
    public var uuid: UUID
    public var properties: BleCharacteristicProperties
    
    public var underlyingValue: ByteArray
    override public var value: ByteArray {
        get {
            return underlyingValue
        }
        set(value) {
            underlyingValue = value
            update()
        }
    }
    public var subscribers: Dictionary<String, BleClient>
    
    public func onSubscribe(from: BleClient) -> Void {
        subscribers[ from.info.id ] = from
    }
    public func onSubscribe(_ from: BleClient) -> Void {
        return onSubscribe(from: from)
    }
    
    public func onUnsubscribe(from: BleClient) -> Void {
        subscribers.remove(from.info.id)
    }
    public func onUnsubscribe(_ from: BleClient) -> Void {
        return onUnsubscribe(from: from)
    }
    
    public func onRead(from: BleClient, request: RequestId) -> Void {
        from.respond(request, value, BleResponseStatus.success)
    }
    public func onRead(_ from: BleClient, _ request: RequestId) -> Void {
        return onRead(from: from, request: request)
    }
    
    public func onWrite(from: BleClient, request: RequestId, value: ByteArray) -> Void {
        self.value = value
        from.respond(request, value, BleResponseStatus.success)
    }
    public func onWrite(_ from: BleClient, _ request: RequestId, _ value: ByteArray) -> Void {
        return onWrite(from: from, request: request, value: value)
    }
    
    override public func update() -> Void {
        var value = underlyingValue
        underlyingEvent.onNext(boxWrap(value))
        
        for sub in subscribers {
            indicate(sub.value, value)
        }
    }
    public var underlyingEvent: PublishSubject<Box<ByteArray>>
    override public var onChange: Observable<Box<ByteArray>> {
        get {
            return underlyingEvent
        }
    }
    
    public init(serviceUuid: UUID, uuid: UUID, value: ByteArray, properties: BleCharacteristicProperties = BleCharacteristicProperties(read: true, write: true, notify: true)) {
        self.serviceUuid = serviceUuid
        self.uuid = uuid
        self.properties = properties
        let underlyingValue: ByteArray = value
        self.underlyingValue = underlyingValue
        let subscribers: Dictionary<String, BleClient> = Dictionary()
        self.subscribers = subscribers
        let underlyingEvent: PublishSubject<Box<ByteArray>> = PublishSubject.create()
        self.underlyingEvent = underlyingEvent
        super.init()
    }
    convenience public init(_ serviceUuid: UUID, _ uuid: UUID, _ value: ByteArray, _ properties: BleCharacteristicProperties = BleCharacteristicProperties(read: true, write: true, notify: true)) {
        self.init(serviceUuid: serviceUuid, uuid: uuid, value: value, properties: properties)
    }
}
 
 
 
 

public protocol BleServer: Closeable {
    
    var clients: ObservableProperty<Dictionary<String, BleClient>> { get }
    
    var characteristics: Dictionary<UUID, Dictionary<UUID, BleCharacteristicServer>> { get }
}
 
 

public protocol BleClient {
    
    var info: BleDeviceInfo { get }
    
    var connected: Bool { get }
    
    func respond(request: RequestId, data: ByteArray, status: BleResponseStatus) -> Void
    func respond(_ request: RequestId, _ data: ByteArray, _ status: BleResponseStatus) -> Void
    
    func notify(service: UUID, characteristic: UUID, value: ByteArray) -> Void
    func notify(_ service: UUID, _ characteristic: UUID, _ value: ByteArray) -> Void
    
    func indicate(service: UUID, characteristic: UUID, value: ByteArray) -> Void
    func indicate(_ service: UUID, _ characteristic: UUID, _ value: ByteArray) -> Void
}
 
 
 

public typealias RequestId = Int32

public enum BleResponseStatus: String, StringEnum, CaseIterable, Codable {
    case success = "success"
    case invalidHandle = "invalidHandle"
    case readNotPermitted = "readNotPermitted"
    case writeNotPermitted = "writeNotPermitted"
    case invalidPdu = "invalidPdu"
    case insufficientAuthentication = "insufficientAuthentication"
    case requestNotSupported = "requestNotSupported"
    case invalidOffset = "invalidOffset"
    case insufficientAuthorization = "insufficientAuthorization"
    case prepareQueueFull = "prepareQueueFull"
    case attributeNotFound = "attributeNotFound"
    case attributeNotLong = "attributeNotLong"
    case insufficientEncryptionKeySize = "insufficientEncryptionKeySize"
    case invalidAttributeValueLength = "invalidAttributeValueLength"
    case unlikelyError = "unlikelyError"
    case insufficientEncryption = "insufficientEncryption"
    case unsupportedGroupType = "unsupportedGroupType"
    case insufficientResources = "insufficientResources"
    public init(from decoder: Decoder) throws {
        self = try BleResponseStatus(rawValue: decoder.singleValueContainer().decode(RawValue.self)) ?? .success
    }
}
 

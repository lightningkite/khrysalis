import Foundation
import Khrysalis
import CoreBluetooth
import RxBluetoothKit
import RxSwift

//--- RequestId
public typealias RequestId = CBATTRequest

//--- BleResponseStatus.{
//--- BleResponseStatus.Primary Constructor
//--- BleResponseStatus.}
public typealias BleResponseStatus = CBATTError.Code


//--- Ble.{ (overwritten on flow generation)
public enum Ble {
    
    //--- Manager
    static let manager = CentralManager(queue: .main)
    public static func managerOnceObs(_ viewDependency: ViewDependency) -> Observable<CentralManager> {
        return Observable.just(manager)
    }

    //--- Ble.notificationDescriptorUuid
    public static var notificationDescriptorUuid: UUID = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb")

    //--- Ble.serve(ViewDependency, List<BleCharacteristicServer>, List<UUID>? , Float)
    public static func serve(_ viewDependency: ViewDependency, _ characteristics: Array<BleCharacteristicServer>, _ serviceUuids: Array<UUID>? , _ advertisingIntensity: Float) -> BleServer {
        TODO()
    }
    public static func serve(viewDependency: ViewDependency, characteristics: Array<BleCharacteristicServer>, serviceUuids: Array<UUID>? , advertisingIntensity: Float) -> BleServer {
        return serve(viewDependency, characteristics, serviceUuids, advertisingIntensity)
    }

    //--- Ble.scan(ViewDependency, List<UUID>, Float)
    public static func scan(_ viewDependency: ViewDependency, _ withServices: Array<UUID> = [], _ intensity: Float = 0.5) -> Observable<BleScanResult> {
        return managerOnceObs(viewDependency)
            .switchMap { manager in manager.scanForPeripherals(withServices: withServices.isEmpty ? nil : withServices.map { CBUUID(nsuuid: $0) }) }
            .map { it in BleScanResult(info: BleDeviceInfo(id: it.peripheral.identifier.uuidString, name: it.peripheral.name), rssi: Int32(it.rssi)) }
    }
    public static func scan(viewDependency: ViewDependency, withServices: Array<UUID> = [], intensity: Float = 0.5) -> Observable<BleScanResult> {
        return scan(viewDependency, withServices, intensity)
    }

    //--- Ble.connect(ViewDependency, String)
    public static func connect(_ viewDependency: ViewDependency, _ deviceId: String) -> Observable<BleConnection> {
        return managerOnceObs(viewDependency)
            .flatMap { (manager) -> Observable<Peripheral> in
                if let p = manager.retrievePeripherals(withIdentifiers: [UUID.fromString(deviceId)]).firstOrNull() {
                    return manager.establishConnection(p)
                } else {
                    throw IllegalStateException("No known peripheral with id \(deviceId)")
                }
            }
        .map { it in BleConnectionImpl(peripheral: it) }
    }
    public static func connect(viewDependency: ViewDependency, deviceId: String) -> Observable<BleConnection> {
        return connect(viewDependency, deviceId)
    }

    //--- Ble.} (overwritten on flow generation)
}

//--- other

extension BleCharacteristic: CharacteristicIdentifier {
    public var service: ServiceIdentifier { BleService(serviceUuid) }
    public var uuid: CBUUID { CBUUID(nsuuid: self.characteristicUuid) }
}

extension BleService: ServiceIdentifier {
    public var uuid: CBUUID { CBUUID(nsuuid: self.serviceUuid) }
}

extension BleDescriptor: DescriptorIdentifier {
    public var uuid: CBUUID { CBUUID(nsuuid: self.descriptorUuid) }
    public var characteristic: CharacteristicIdentifier {
        BleCharacteristic(serviceUuid: self.serviceUuid, characteristicUuid: self.characteristicUuid)
    }
}

public class BleServerImpl: NSObject, BleServer, CBPeripheralManagerDelegate {

    var manager: CBPeripheralManager!
    public var characteristics: Dictionary<UUID, Dictionary<UUID, BleCharacteristicServer>>
    var rawCharacteristics: Dictionary<UUID, Dictionary<UUID, CBMutableCharacteristic>> = [:]
    var serviceUuids: Array<UUID>?
    var advertisingIntensity: Float
    public init(characteristics: Array<BleCharacteristicServer>, serviceUuids: Array<UUID>?, advertisingIntensity: Float) {
        self.characteristics = characteristics
            .groupBy { $0.characteristic.serviceUuid }
            .mapValues { $0.associate { ($0.characteristic.characteristicUuid, $0) } }
        self.serviceUuids = serviceUuids
        self.advertisingIntensity = advertisingIntensity
        super.init()
        self.manager = CBPeripheralManager(delegate: self, queue: .main)

        for (serviceUuid, chars) in self.characteristics {
            var service = CBMutableService(type: CBUUID(nsuuid: serviceUuid), primary: true)
            var serviceChars = [CBCharacteristic]()
            for (charUuid, char) in chars {

                var properties: CBCharacteristicProperties = []
                if char.properties.broadcast {
                    properties.insert(.broadcast)
                }
                if char.properties.read {
                    properties.insert(.read)
                }
                if char.properties.writeWithoutResponse {
                    properties.insert(.writeWithoutResponse)
                }
                if char.properties.write {
                    properties.insert(.write)
                }
                if char.properties.notify {
                    properties.insert(.notify)
                }
                if char.properties.indicate {
                    properties.insert(.indicate)
                }
                if char.properties.authenticatedSignedWrites {
                    properties.insert(.authenticatedSignedWrites)
                }
                if char.properties.extendedProperties {
                    properties.insert(.extendedProperties)
                }

                var permissions: CBAttributePermissions = []
                if char.properties.read {
                    permissions.insert(.readable)
                }
                if char.properties.writeWithoutResponse {
                    permissions.insert(.writeable)
                }
                if char.properties.write {
                    permissions.insert(.writeable)
                }
                if char.properties.notify {
                    permissions.insert(.readable)
                }
                if char.properties.indicate {
                    permissions.insert(.readable)
                }
                if char.properties.authenticatedSignedWrites {
                    permissions.insert(.writeEncryptionRequired)
                }

                var char = CBMutableCharacteristic(
                    type: CBUUID(nsuuid: charUuid),
                    properties: properties,
                    value: Data(),
                    permissions: permissions
                )
                rawCharacteristics.getOrPut(serviceUuid){ [:] }
                rawCharacteristics[serviceUuid]![charUuid] = char
                serviceChars.append(char)
            }
            service.characteristics = serviceChars
            self.manager.add(service)
        }
    }
    
    func clientFor(device: CBCentral) -> BleClient {
        return BleClientImpl(server: self, device: device)
    }

    private var _advertising: Bool = false
    public var advertising: Bool {
        get {
            return _advertising
        }
        set(value){
            if value && !_advertising {
                manager.startAdvertising([CBAdvertisementDataServiceUUIDsKey: (serviceUuids ?? self.characteristics.keys.toList()).map { CBUUID(nsuuid: $0) }] )
            } else if !value {
                manager.stopAdvertising()
            }
            _advertising = value
        }
    }

    public func dispose(){
        advertising = false
    }
    
    public func peripheralManagerDidUpdateState(_ peripheral: CBPeripheralManager) {
        
    }
    
    public func peripheralManager(_ peripheral: CBPeripheralManager, didReceiveRead request: CBATTRequest) {
        let serviceUuid = UUID(uuidString: request.characteristic.service.uuid.uuidString)!
        let charUuid = UUID(uuidString: request.characteristic.uuid.uuidString)!
        guard let service = self.characteristics[serviceUuid] else { return }
        guard let characteristic = service[charUuid] else { return }
        characteristic.onRead(clientFor(device: request.central), request)
    }
    
    public func peripheralManager(_ peripheral: CBPeripheralManager, didReceiveWrite requests: [CBATTRequest]) {
        for request in requests {
            let serviceUuid = UUID(uuidString: request.characteristic.service.uuid.uuidString)!
            let charUuid = UUID(uuidString: request.characteristic.uuid.uuidString)!
            guard let service = self.characteristics[serviceUuid] else { return }
            guard let characteristic = service[charUuid] else { return }
            characteristic.onWrite(clientFor(device: request.central), request, request.value ?? Data())
        }
    }
    
    public func peripheralManager(_ peripheral: CBPeripheralManager, central: CBCentral, didSubscribeTo characteristic: CBCharacteristic) {
        let serviceUuid = UUID(uuidString: characteristic.service.uuid.uuidString)!
        let charUuid = UUID(uuidString: characteristic.uuid.uuidString)!
        guard let service = self.characteristics[serviceUuid] else { return }
        guard let characteristic = service[charUuid] else { return }
        characteristic.onSubscribe(clientFor(device: central))
    }
    
    public func peripheralManager(_ peripheral: CBPeripheralManager, central: CBCentral, didUnsubscribeFrom characteristic: CBCharacteristic) {
        let serviceUuid = UUID(uuidString: characteristic.service.uuid.uuidString)!
        let charUuid = UUID(uuidString: characteristic.uuid.uuidString)!
        guard let service = self.characteristics[serviceUuid] else { return }
        guard let characteristic = service[charUuid] else { return }
        characteristic.onUnsubscribe(clientFor(device: central))
    }
}

public class BleClientImpl: BleClient {
    private var server: BleServerImpl
    private var device: CBCentral
    public init(server: BleServerImpl, device: CBCentral) {
        self.server = server
        self.device = device
    }
    
    public var info: BleDeviceInfo {
        BleDeviceInfo(id: device.identifier.toString(), name: nil)
    }
    
    public var connected: Bool = true
    
    public func respond(request: RequestId, data: Data, status: BleResponseStatus) {
        respond(request, data, status)
    }
    
    public func respond(_ request: RequestId, _ data: Data, _ status: BleResponseStatus) {
        request.value = data
        server.manager.respond(to: request, withResult: status)
    }
    
    public func notify(service: UUID, characteristic: UUID, value: Data) {
        notify(service, characteristic, value)
    }
    
    public func notify(_ service: UUID, _ characteristic: UUID, _ value: Data) {
        server.manager.updateValue(value, for: server.rawCharacteristics[service]![characteristic]!, onSubscribedCentrals: [device])
    }
    
    public func indicate(service: UUID, characteristic: UUID, value: Data) {
        indicate(service, characteristic, value)
    }
    
    public func indicate(_ service: UUID, _ characteristic: UUID, _ value: Data) {
        server.manager.updateValue(value, for: server.rawCharacteristics[service]![characteristic]!, onSubscribedCentrals: [device])
    }
    
    
}

public class BleConnectionImpl: BleConnection {

    public var peripheral: Peripheral
    public init(peripheral: Peripheral){
        self.peripheral = peripheral
    }

    public var deviceInfo: BleDeviceInfo {
        return BleDeviceInfo(id: peripheral.identifier.uuidString, name: peripheral.name)
    }

    public var mtu: Int32 { return Int32(peripheral.maximumWriteValueLength(for: .withResponse)) }

    public func readRssi() -> Single<Int32> {
        return peripheral.readRSSI().map { Int32($0.1) }
    }

    public func read(characteristic: BleCharacteristic) -> Single<Data> {
        return read(characteristic)
    }

    public func read(_ characteristic: BleCharacteristic) -> Single<Data> {
        return peripheral.readValue(for: characteristic).map { $0.value! }
    }

    public func write(characteristic: BleCharacteristic, value: Data) -> Single<Data> {
        return write(characteristic, value)
    }

    public func write(_ characteristic: BleCharacteristic, _ value: Data) -> Single<Data> {
        return peripheral.writeValue(value, for: characteristic, type: .withResponse).map { $0.value! }
    }

    public func notify(characteristic: BleCharacteristic) -> Observable<Data> {
        return notify(characteristic)
    }

    public func notify(_ characteristic: BleCharacteristic) -> Observable<Data> {
        return peripheral.observeValueUpdate(for: characteristic).map { $0.value! }
    }

    public func indicate(characteristic: BleCharacteristic) -> Observable<Data> {
        return indicate(characteristic)
    }

    public func indicate(_ characteristic: BleCharacteristic) -> Observable<Data> {
        return peripheral.observeValueUpdate(for: characteristic).map { $0.value! }
    }

    public func read(descriptor: BleDescriptor) -> Single<Data> {
        return read(descriptor)
    }

    public func read(_ descriptor: BleDescriptor) -> Single<Data> {
        return peripheral.readValue(for: descriptor).map { $0.characteristic.value! }
    }

    public func write(descriptor: BleDescriptor, value: Data) -> Single<Data> {
        return write(descriptor, value)
    }

    public func write(_ descriptor: BleDescriptor, _ value: Data) -> Single<Data> {
        return peripheral.writeValue(value, for: descriptor).map { it in
            if let x = it.descriptor.value as? NSData {
                return x as Data
            } else {
                throw IllegalStateException("Values is not Data!  Instead found \(it.descriptor.value)")
            }
        }
    }

}

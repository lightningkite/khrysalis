import CoreBluetooth
import RxSwift
import RxRelay

//--- Ble.{ (overwritten on flow generation)
public enum Ble {
    
    //--- Ble.notificationDescriptorUuid (overwritten on flow generation)
    public static var notificationDescriptorUuid: UUID {
        TODO()
    }
    
    //--- Ble.serve(ViewDependency, List<BleCharacteristicServer>, List<UUID>? , Float) (overwritten on flow generation)
    public static func serve(_ viewDependency: ViewDependency, _ characteristics: Array<BleCharacteristicServer>, _ serviceUuids: Array<UUID>? , _ advertisingIntensity: Float) -> BleServer {
        TODO()
    }
    public static func serve(viewDependency: ViewDependency, characteristics: Array<BleCharacteristicServer>, serviceUuids: Array<UUID>? , advertisingIntensity: Float) -> BleServer {
        return serve(viewDependency, characteristics, serviceUuids, advertisingIntensity)
    }
    
    //--- Ble.scan(ViewDependency, List<UUID>, Float, (info:BleDeviceInfo)->Unit) (overwritten on flow generation)
    public static func scan(_ viewDependency: ViewDependency, _ withServices: Array<UUID>, _ intensity: Float, _ onDeviceFound: (_ info: BleDeviceInfo) -> Void) -> Disposable {
        TODO()
    }
    public static func scan(viewDependency: ViewDependency, withServices: Array<UUID>, intensity: Float, onDeviceFound: (_ info: BleDeviceInfo) -> Void) -> Disposable {
        return scan(viewDependency, withServices, intensity, onDeviceFound)
    }
    
    //--- Ble.connect(ViewDependency, String) (overwritten on flow generation)
    public static func connect(_ viewDependency: ViewDependency, _ deviceId: String) -> Observable<BleDevice> {
        TODO()
    }
    public static func connect(viewDependency: ViewDependency, deviceId: String) -> Observable<BleDevice> {
        return connect(viewDependency, deviceId)
    }
    
    //--- Ble.stayConnected(ViewDependency, String) (overwritten on flow generation)
    public static func stayConnected(_ viewDependency: ViewDependency, _ deviceId: String) -> Observable<BleDevice> {
        TODO()
    }
    public static func stayConnected(viewDependency: ViewDependency, deviceId: String) -> Observable<BleDevice> {
        return stayConnected(viewDependency, deviceId)
    }
    
    //--- Ble.connectBackground(String) (overwritten on flow generation)
    public static func connectBackground(_ deviceId: String) -> Observable<BleDevice> {
        TODO()
    }
    public static func connectBackground(deviceId: String) -> Observable<BleDevice> {
        return connectBackground(deviceId)
    }
    
    //--- Ble.stayConnectedBackground(String) (overwritten on flow generation)
    public static func stayConnectedBackground(_ deviceId: String) -> Observable<BleDevice> {
        TODO()
    }
    public static func stayConnectedBackground(deviceId: String) -> Observable<BleDevice> {
        return stayConnectedBackground(deviceId)
    }
    
    //--- Ble.} (overwritten on flow generation)
}


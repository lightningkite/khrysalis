//Package: com.lightningkite.khrysalis.bluetooth
//Converted using Khrysalis2

import Foundation
import RxSwift
import RxRelay
import Khrysalis



public class PropertyBleCharacteristicServer: MutableObservableProperty<Data>, BleCharacteristicServer {
    
    public var characteristic: BleCharacteristic
    public var properties: BleCharacteristicProperties
    
    public var underlyingValue: Data
    override public var value: Data {
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
    
    public func onDisconnect(from: BleClient) -> Void {
        subscribers.remove(from.info.id)
    }
    public func onDisconnect(_ from: BleClient) -> Void {
        return onDisconnect(from: from)
    }
    
    public func onRead(from: BleClient, request: RequestId) -> Void {
        from.respond(request, value, BleResponseStatus.success)
    }
    public func onRead(_ from: BleClient, _ request: RequestId) -> Void {
        return onRead(from: from, request: request)
    }
    
    public func onWrite(from: BleClient, request: RequestId, value: Data) -> Void {
        self.value = value
        from.respond(request, value, BleResponseStatus.success)
    }
    public func onWrite(_ from: BleClient, _ request: RequestId, _ value: Data) -> Void {
        return onWrite(from: from, request: request, value: value)
    }
    
    override public func update() -> Void {
        var value = underlyingValue
        underlyingEvent.onNext(boxWrap(value))
        
        for sub in subscribers {
            indicate(sub.value, value)
        }
    }
    public var underlyingEvent: PublishSubject<Box<Data>>
    override public var onChange: Observable<Box<Data>> {
        get {
            return underlyingEvent.observeOn(AndroidSchedulers.mainThread())
        }
    }
    
    public init(characteristic: BleCharacteristic, value: Data, properties: BleCharacteristicProperties = BleCharacteristicProperties(read: true, write: true, notify: true)) {
        self.characteristic = characteristic
        self.properties = properties
        let underlyingValue: Data = value
        self.underlyingValue = underlyingValue
        let subscribers: Dictionary<String, BleClient> = Dictionary()
        self.subscribers = subscribers
        let underlyingEvent: PublishSubject<Box<Data>> = PublishSubject.create()
        self.underlyingEvent = underlyingEvent
        super.init()
    }
    convenience public init(_ characteristic: BleCharacteristic, _ value: Data, _ properties: BleCharacteristicProperties = BleCharacteristicProperties(read: true, write: true, notify: true)) {
        self.init(characteristic: characteristic, value: value, properties: properties)
    }
}
 

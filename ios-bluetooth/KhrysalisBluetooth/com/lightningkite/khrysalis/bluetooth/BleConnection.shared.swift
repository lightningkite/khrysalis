//Package: com.lightningkite.khrysalis.bluetooth
//Converted using Khrysalis2

import Foundation
import RxSwift
import RxRelay
import Khrysalis



public protocol BleConnection {
    
    var deviceInfo: BleDeviceInfo { get }
    
    var mtu: Int32 { get }
    
    func readRssi() -> Single<Int32>
    
    func read(characteristic: BleCharacteristic) -> Single<Data>
    func read(_ characteristic: BleCharacteristic) -> Single<Data>
    
    func write(characteristic: BleCharacteristic, value: Data) -> Single<Data>
    func write(_ characteristic: BleCharacteristic, _ value: Data) -> Single<Data>
    
    func notify(characteristic: BleCharacteristic) -> Observable<Data>
    func notify(_ characteristic: BleCharacteristic) -> Observable<Data>
    
    func indicate(characteristic: BleCharacteristic) -> Observable<Data>
    func indicate(_ characteristic: BleCharacteristic) -> Observable<Data>
    
    func read(descriptor: BleDescriptor) -> Single<Data>
    func read(_ descriptor: BleDescriptor) -> Single<Data>
    
    func write(descriptor: BleDescriptor, value: Data) -> Single<Data>
    func write(_ descriptor: BleDescriptor, _ value: Data) -> Single<Data>
}
 
 

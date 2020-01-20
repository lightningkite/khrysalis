//Package: com.lightningkite.kwift.bluetooth
//Converted using Kwift2

import Foundation
import RxSwift
import RxRelay



public protocol BleDevice {
    
    var connected: ObservableProperty<Bool> { get }
    
    var rssi: ObservableProperty<Int32> { get }
    
    var mtu: ObservableProperty<Int32> { get }
    
    func requestMtu(mtu: Int32) -> Observable<Void>
    func requestMtu(_ mtu: Int32) -> Observable<Void>
    
    func read(serviceId: UUID, characteristicId: UUID) -> Observable<ByteArray>
    func read(_ serviceId: UUID, _ characteristicId: UUID) -> Observable<ByteArray>
    
    func write(serviceId: UUID, characteristicId: UUID, value: ByteArray) -> Observable<Void>
    func write(_ serviceId: UUID, _ characteristicId: UUID, _ value: ByteArray) -> Observable<Void>
    
    func subscribe(serviceId: UUID, characteristicId: UUID, indicate: Bool) -> Observable<ByteArray>
    func subscribe(_ serviceId: UUID, _ characteristicId: UUID, _ indicate: Bool) -> Observable<ByteArray>
    
    func close() -> Void
}
 

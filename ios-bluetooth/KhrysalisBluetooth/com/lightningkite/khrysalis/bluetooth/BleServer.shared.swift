//Package: com.lightningkite.khrysalis.bluetooth
//Converted using Khrysalis2

import Foundation
import RxSwift
import RxRelay
import Khrysalis



public protocol BleServer: Disposable {
    
    var characteristics: Dictionary<UUID, Dictionary<UUID, BleCharacteristicServer>> { get }
    
    var advertising: Bool { get set }
}
 
 

public protocol BleClient {
    
    var info: BleDeviceInfo { get }
    
    var connected: Bool { get }
    
    func respond(request: RequestId, data: Data, status: BleResponseStatus) -> Void
    func respond(_ request: RequestId, _ data: Data, _ status: BleResponseStatus) -> Void
    
    func notify(service: UUID, characteristic: UUID, value: Data) -> Void
    func notify(_ service: UUID, _ characteristic: UUID, _ value: Data) -> Void
    
    func indicate(service: UUID, characteristic: UUID, value: Data) -> Void
    func indicate(_ service: UUID, _ characteristic: UUID, _ value: Data) -> Void
}
 

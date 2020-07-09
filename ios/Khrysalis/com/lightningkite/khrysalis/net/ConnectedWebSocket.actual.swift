import RxSwift
import RxRelay
import Starscream

//--- ConnectedWebSocket.{
public final class ConnectedWebSocket: WebSocketDelegate, Disposable {
    
    
    //--- ConnectedWebSocket.ownConnection
    public var ownConnection = PublishSubject<ConnectedWebSocket>.create()
    var underlyingSocket: WebSocket?
    var url: String
    //--- ConnectedWebSocket.read
    private let _read: PublishSubject<WebSocketFrame> = PublishSubject.create()
    public var read: Observable<WebSocketFrame> { return _read }
    
    //--- ConnectedWebSocket.Primary Constructor
    init(url: String) {
        self.url = url
    }
    
    
    //--- ConnectedWebSocket.onComplete()
    public func onComplete() -> Void {
        underlyingSocket?.disconnect(closeCode: 1000)
    }
    
    //--- ConnectedWebSocket.onNext(WebSocketFrame)
    public func onNext(_ t: WebSocketFrame) -> Void {
        if let text = t.text {
            underlyingSocket?.write(string: text, completion: nil)
        }
        if let binary = t.binary {
            underlyingSocket?.write(data: binary, completion: nil)
        }
    }
    public func onNext(t: WebSocketFrame) -> Void {
        return onNext(t)
    }
    
    //--- ConnectedWebSocket.onError(Throwable)
    public func onError(_ e: Error) -> Void {
        underlyingSocket?.disconnect(closeCode: 1011)
    }
    public func onError(e: Error) -> Void {
        return onError(e)
    }
    
    //--- Receive
    public func didReceive(event: WebSocketEvent, client: WebSocket) {
        switch event {
        case .binary(let data):
            print("Socket to \(url) got binary message of length '\(data.count)'")
            _read.onNext(WebSocketFrame(binary: data))
            break
        case .text(let string):
            print("Socket to \(url) got message '\(string)'")
            _read.onNext(WebSocketFrame(text: string))
            break
        case .connected(let headers):
            print("Socket to \(url) opened successfully.")
            ownConnection.onNext(self)
            break
        case .disconnected(let reason, let code):
            print("Socket to \(url) disconnecting with code \(code). Reason: \(reason)")
            ownConnection.onCompleted()
            _read.onComplete()
            break
        case .error(let error):
            print("Socket to \(url) failed with error \(error)")
            ownConnection.onError(error ?? Exception())
            read.onError(error ?? Exception())
            break
        case .cancelled:
            print("Socket to \(url) cancelled")
            ownConnection.onError(Exception("Socket connection cancelled."))
            _read.onComplete()
            break
        default:
            break
        }
    }
    
    public func dispose() {
        print("Socket to \(url) was disposed, closing with OK code.")
        underlyingSocket?.disconnect(closeCode: 1000)
        ownConnection.onCompleted()
        _read.onComplete()
    }
    
    //--- ConnectedWebSocket.}
}

extension WebSocketFrame: CustomStringConvertible {
    public var description: String {
        return text ?? "<binary>"
    }
}

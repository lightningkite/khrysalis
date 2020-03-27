import RxSwift
import RxRelay
import Starscream

//--- HttpClient.webSocket(String, Map<String,String>)
public extension HttpClient {
    static func webSocket(_ url: String, _ headers: Dictionary<String, String> = [:]) -> Observable<ConnectedWebSocket> {
        return Observable.using({ () -> ConnectedWebSocket in
            var out = ConnectedWebSocket(url: url)
            var request = URLRequest(url: URL(string: url)!)
            for (key, value) in headers {
                request.setValue(value, forHTTPHeaderField: key)
            }
            let socket = WebSocket(request: request)
            socket.delegate = out
            out.underlyingSocket = socket
            socket.connect()
            return out
        }, observableFactory: { $0.ownConnection })
    }
    static func webSocket(url: String, headers: Dictionary<String, String> = [:]) -> Observable<ConnectedWebSocket> {
        return webSocket(url, headers)
    }
}

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
    public func onError(_ e: Throwable) -> Void {
        underlyingSocket?.disconnect(closeCode: 1011)
    }
    public func onError(e: Throwable) -> Void {
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

//--- WebSocketFrame.{
public struct WebSocketFrame: CustomStringConvertible {
    
    public let binary: Data?
    public let text: String?
    
    //--- WebSocketFrame.Primary Constructor
    public init(binary: Data? = nil, text: String? = nil) {
        self.binary = binary
        self.text = text
    }
    
    //--- WebSocketFrame.toString()
    public var description: String {
        return text ?? "<binary>"
    }
    
    //--- WebSocketFrame.}
}

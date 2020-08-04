import { Observer, Subject, Unsubscribable } from 'rxjs';
import { WebSocketFrame } from './WebSocketFrame.actual';
export declare class ConnectedWebSocket implements Observer<WebSocketFrame>, Unsubscribable {
    static implementsInterfaceIoReactivexObserver: boolean;
    readonly url: string;
    constructor(url: string);
    underlyingSocket: (WebSocket | null);
    private resetSocket;
    readonly read: Subject<WebSocketFrame>;
    readonly ownConnection: Subject<ConnectedWebSocket>;
    closed: boolean;
    complete(): void;
    next(t: WebSocketFrame): void;
    error(e: any): void;
    unsubscribe(): void;
}

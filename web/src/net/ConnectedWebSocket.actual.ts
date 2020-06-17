// Generated by Khrysalis TypeScript converter
// File: net/ConnectedWebSocket.actual.kt
// Package: com.lightningkite.khrysalis.net
import {Observable, Observer, Subject, SubscriptionLike, Unsubscribable} from 'rxjs'
import { WebSocketFrame } from './WebSocketFrame.actual'

//! Declares com.lightningkite.khrysalis.net.ConnectedWebSocket
export class ConnectedWebSocket implements Observer<WebSocketFrame>, Unsubscribable {
    public static implementsInterfaceIoReactivexObserver = true;
    public readonly url: string;
    public constructor(url: string) {
        this.url = url;
    }

    underlyingSocket: (WebSocket | null) = null;

    private resetSocket(){
        this.underlyingSocket?.close(1000, "Resetting connection");
        const newSocket = new WebSocket(this.url);
        const parent = this;
        newSocket.binaryType = "arraybuffer";
        newSocket.addEventListener("open", (event)=>{
            parent.ownConnection.next(this);
        });
        newSocket.addEventListener("error", (event)=>{
            parent.ownConnection.error(event);
            parent.read.error(event);
        });
        newSocket.addEventListener("close", (event)=>{
            parent.ownConnection.complete();
            parent.read.complete();
        });
        newSocket.addEventListener("message", (event: MessageEvent)=>{
            const d = event.data;
            if(typeof d === "string"){
                parent.read.next(new WebSocketFrame(null, d));
            } else {
                parent.read.next(new WebSocketFrame(new Int8Array(d as ArrayBuffer), null))
            }
        });
        this.underlyingSocket = newSocket;
    }

    public readonly read: Subject<WebSocketFrame> = new Subject();
    public readonly ownConnection = new Subject<ConnectedWebSocket>();

    closed: boolean = false;
    
    public complete() {
        this.underlyingSocket?.close(1000, null);
        this.closed = true;
    }
    
    public next(t: WebSocketFrame) {
        this.underlyingSocket.send(t.text ?? t.binary.buffer)
    }
    
    public error(e: any) {
        this.underlyingSocket?.close(1011, e.message);
        this.closed = true;
    }

    public unsubscribe() {
        this.complete();
    }
}

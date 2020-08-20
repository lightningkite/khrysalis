import { Observable, SchedulerLike } from 'rxjs';
import { ConnectedWebSocket } from "./ConnectedWebSocket.actual";
import { HttpBody } from "./HttpBody.actual";
export declare class HttpClient {
    static INSTANCE: HttpClient;
    readonly GET = "GET";
    readonly POST = "POST";
    readonly PUT = "PUT";
    readonly PATCH = "PATCH";
    readonly DELETE = "DELETE";
    ioScheduler: SchedulerLike | null;
    responseScheduler: SchedulerLike | null;
    timeout: number;
    call(url: string, method?: string, headers?: Map<string, string>, body?: (HttpBody | null)): Observable<Response>;
    webSocket(url: string): Observable<ConnectedWebSocket>;
}

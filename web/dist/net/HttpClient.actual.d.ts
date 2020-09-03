import { Observable, SchedulerLike } from 'rxjs';
import { ConnectedWebSocket } from "./ConnectedWebSocket.actual";
import { HttpBody } from "./HttpBody.actual";
import { HttpOptions, HttpProgress } from "./HttpModels.shared";
import { ObservableProperty } from "../observables/ObservableProperty.shared";
export declare class HttpClient {
    static INSTANCE: HttpClient;
    readonly GET = "GET";
    readonly POST = "POST";
    readonly PUT = "PUT";
    readonly PATCH = "PATCH";
    readonly DELETE = "DELETE";
    ioScheduler: SchedulerLike | null;
    responseScheduler: SchedulerLike | null;
    defaultOptions: HttpOptions;
    call(url: string, method?: string, headers?: Map<string, string>, body?: (HttpBody | null), options?: HttpOptions): Observable<Response>;
    callWithProgress(url: string, method?: string, headers?: Map<string, string>, body?: (HttpBody | null), options?: HttpOptions): [ObservableProperty<HttpProgress>, Observable<Response>];
    webSocket(url: string): Observable<ConnectedWebSocket>;
}

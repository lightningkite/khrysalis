import { Observable } from 'rxjs';
export declare function ioReactivexSingleUnsuccessfulAsError(this_: Observable<Response>): Observable<Response>;
export declare function ioReactivexSingleReadJson<T>(this_: Observable<Response>, T: Array<any>): Observable<T>;
export declare function ioReactivexSingleReadText(this_: Observable<Response>): Observable<String>;
export declare function ioReactivexSingleReadData(this_: Observable<Response>): Observable<Int8Array>;
export declare function ioReactivexSingleReadHttpException<Element>(this_: Observable<Element>): Observable<Element>;

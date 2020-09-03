import { Codable } from '../Codable.actual';
import { HttpMediaType } from './HttpMediaType.actual';
import { Observable } from "rxjs";
import { Image } from "../Image.shared";
export declare class HttpBody {
    data: BodyInit;
    type: HttpMediaType;
    constructor(data: BodyInit, type: HttpMediaType);
}
export declare class HttpBodyPart {
    name: string;
    value: string | null;
    filename: string | null;
    body: Blob;
}
export declare function xAnyToJsonHttpBody(this_: Codable): HttpBody;
export declare function xByteArrayToHttpBody(this_: ArrayBuffer, mediaType: HttpMediaType): HttpBody;
export declare function xStringToHttpBody(this_: string, mediaType?: HttpMediaType): HttpBody;
export declare function xUriToHttpBody(this_: File): HttpBody;
export declare function xImageToHttpBody(this_: Image, maxDimension?: number): Observable<HttpBody>;
export declare function multipartFormBody(...parts: HttpBodyPart[]): HttpBody;
export declare function multipartFormFilePart(name: string, valueOrFilename?: string, body?: Blob): HttpBodyPart;

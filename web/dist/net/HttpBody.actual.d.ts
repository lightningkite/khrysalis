import { Codable } from '../Codable.actual';
import { HttpMediaType } from './HttpMediaType.actual';
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
export declare function comLightningkiteKhrysalisCodableToJsonHttpBody(this_: Codable): HttpBody;
export declare function kotlinByteArrayToHttpBody(this_: ArrayBuffer, mediaType: HttpMediaType): HttpBody;
export declare function kotlinStringToHttpBody(this_: string, mediaType?: HttpMediaType): HttpBody;
export declare function multipartFormBody(...parts: HttpBodyPart[]): HttpBody;
export declare function multipartFormFilePart(name: string, valueOrFilename?: string, body?: Blob): HttpBodyPart;

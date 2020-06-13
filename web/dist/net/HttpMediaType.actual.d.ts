export declare type HttpMediaType = string;
export declare let HttpMediaType: StringConstructor;
export declare class HttpMediaTypes {
    static INSTANCE: HttpMediaTypes;
    readonly JSON = "application/json";
    readonly TEXT = "text/plain";
    readonly JPEG = "image/jpeg";
    readonly MULTIPART_FORM_DATA = "multipart/form-data";
}

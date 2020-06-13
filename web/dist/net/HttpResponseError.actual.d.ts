import { Exception } from "../Kotlin";
export declare class HttpResponseException extends Exception {
    readonly response: Response;
    constructor(response: Response, cause?: any);
}
export declare class HttpReadResponseException extends Exception {
    readonly response: Response;
    readonly text: string;
    constructor(response: Response, text: string, cause?: any);
}

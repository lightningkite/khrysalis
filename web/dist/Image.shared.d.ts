import { DrawableResource } from './views/DrawableResource';
export declare class Image {
    constructor();
}
export declare class ImageReference extends Image {
    readonly uri: File;
    constructor(uri: File);
    hashCode(): number;
    equals(other: any): boolean;
    toString(): string;
    copy(uri?: File): ImageReference;
}
export declare class ImageImageBitmap extends Image {
    readonly bitmap: ImageBitmap;
    constructor(bitmap: ImageBitmap);
    hashCode(): number;
    equals(other: any): boolean;
    toString(): string;
    copy(bitmap?: ImageBitmap): ImageImageBitmap;
}
export declare class ImageRaw extends Image {
    readonly raw: Int8Array;
    constructor(raw: Int8Array);
    hashCode(): number;
    equals(other: any): boolean;
    toString(): string;
    copy(raw?: Int8Array): ImageRaw;
}
export declare class ImageRemoteUrl extends Image {
    readonly url: string;
    constructor(url: string);
    hashCode(): number;
    equals(other: any): boolean;
    toString(): string;
    copy(url?: string): ImageRemoteUrl;
}
export declare class ImageResource extends Image {
    readonly resource: DrawableResource;
    constructor(resource: DrawableResource);
    hashCode(): number;
    equals(other: any): boolean;
    toString(): string;
    copy(resource?: DrawableResource): ImageResource;
}
export declare function xStringAsImage(this_: string): Image;
export declare function xUriAsImage(this_: File): Image;
export declare function xBitmapAsImage(this_: ImageBitmap): Image;
export declare function xIntAsImage(this_: DrawableResource): Image;

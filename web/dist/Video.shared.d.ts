export declare class Video {
    constructor();
}
export declare class VideoReference extends Video {
    readonly uri: File;
    constructor(uri: File);
    hashCode(): number;
    equals(other: any): boolean;
    toString(): string;
    copy(uri?: File): VideoReference;
}
export declare class VideoRemoteUrl extends Video {
    readonly url: string;
    constructor(url: string);
    hashCode(): number;
    equals(other: any): boolean;
    toString(): string;
    copy(url?: string): VideoRemoteUrl;
}
export declare function xStringAsVideo(this_: string): Video;
export declare function xUriAsVideo(this_: File): Video;

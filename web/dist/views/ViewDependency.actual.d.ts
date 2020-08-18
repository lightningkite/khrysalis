import { Image } from '../Image.shared';
import { GeoCoordinate } from '../location/GeoCoordinate.shared';
export declare function listenForDialogs(): void;
export declare function comLightningkiteKhrysalisAndroidActivityAccessShare(this_: Window, shareTitle: string, message?: (string | null), url?: (string | null), image?: (Image | null)): void;
export declare function comLightningkiteKhrysalisAndroidActivityAccessOpenMap(this_: Window, coordinate: GeoCoordinate, label?: (string | null), zoom?: (number | null)): void;
export declare function comLightningkiteKhrysalisAndroidActivityAccessOpenEvent(this_: Window, title: string, description: string, location: string, start: Date, end: Date): void;
export declare function comLightningkiteKhrysalisAndroidActivityAccessRequestImagesGallery(this_: Window, callback: (a: Array<File>) => void): void;
export declare function comLightningkiteKhrysalisAndroidActivityAccessRequestImageGallery(this_: Window, callback: (a: File) => void): void;
export declare function comLightningkiteKhrysalisAndroidActivityAccessRequestImageCamera(this_: Window, front: boolean | undefined, callback: (a: File) => void): void;

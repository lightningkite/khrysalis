import { LocationResult } from './LocationResult.shared';
export declare function xActivityAccessRequestLocation(this_RequestLocation: Window, accuracyBetterThanMeters: number | undefined, timeoutInSeconds: number | undefined, onResult: (a: (LocationResult | null), b: (string | null)) => void): void;
export declare function xActivityAccessRequestLocationCached(this_RequestLocationCached: Window, accuracyBetterThanMeters: number | undefined, timeoutInSeconds: number | undefined, onResult: (a: (LocationResult | null), b: (string | null)) => void): void;

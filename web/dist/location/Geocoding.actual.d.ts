import { GeoCoordinate } from './GeoCoordinate.shared';
import { GeoAddress } from './GeoAddress.shared';
import { Observable } from "rxjs";
export declare function setGeocodingMethod(method: (this_Geocode: Window, coordinate: GeoCoordinate | string, maxResults: number) => Observable<Array<GeoAddress>>): void;
export declare function xActivityAccessGeocode(this_Geocode: Window, coordinate: GeoCoordinate | string, maxResults?: number): Observable<Array<GeoAddress>>;

import { MutableObservableProperty } from 'khrysalis/dist/observables/MutableObservableProperty.shared';
import { GeoCoordinate } from 'khrysalis/dist/location/GeoCoordinate.shared';
import { ObservableProperty } from 'khrysalis/dist/observables/ObservableProperty.shared';
import { Map } from 'leaflet';
declare const mapSymbol: unique symbol;
declare global {
    interface HTMLDivElement {
        [mapSymbol]: Map;
    }
}
/**
 * Set up a map source.
 * MapBox example:
 * (map, styleString) => {
 *     tileLayer(
 *         `https://api.tiles.mapbox.com/v4/mapbox.streets/{z}/{x}/{y}.png?access_token=${accessToken}`,
 *         {
 *             attribution: `Map data &copy; <a href="https://www.openstreetmap.org/">OpenStreetMap</a> contributors, <a href="https://creativecommons.org/licenses/by-sa/2.0/">CC-BY-SA</a>, Imagery © <a href="https://www.mapbox.com/">Mapbox</a>`,
 *             maxZoom: 18
 *         }
 *     ).addTo(map);
 * }
 */
export declare function setMapConfiguration(source: (m: Map, style: string | null) => void): void;
export declare function getMapAsync(this_: HTMLDivElement, action: (a: Map) => void): void;
export declare function xMapViewBind(this_: HTMLDivElement, dependency: Window, style: string | null): void;
export declare function xMapViewBindView(this_: HTMLDivElement, dependency: Window, position: ObservableProperty<(GeoCoordinate | null)>, zoomLevel?: number, animate?: boolean, style?: string | null): void;
export declare function xMapViewBindSelect(this_: HTMLDivElement, dependency: Window, position: MutableObservableProperty<(GeoCoordinate | null)>, zoomLevel?: number, animate?: boolean, style?: string | null): void;
export {};

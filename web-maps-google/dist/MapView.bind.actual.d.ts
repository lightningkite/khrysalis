import { MutableObservableProperty } from 'khrysalis/dist/observables/MutableObservableProperty.shared';
import { GeoCoordinate } from 'khrysalis/dist/location/GeoCoordinate.shared';
import { ObservableProperty } from 'khrysalis/dist/observables/ObservableProperty.shared';
declare const mapSymbol: unique symbol;
declare global {
    interface HTMLDivElement {
        [mapSymbol]: google.maps.Map;
    }
}
export declare function comGoogleAndroidGmsMapsMapViewBind(this_: HTMLDivElement, dependency: Window, style: string | null): void;
export declare function comGoogleAndroidGmsMapsMapViewBindView(this_: HTMLDivElement, dependency: Window, position: ObservableProperty<(GeoCoordinate | null)>, zoomLevel?: number, animate?: boolean, style?: string | null): void;
export declare function comGoogleAndroidGmsMapsMapViewBindSelect(this_: HTMLDivElement, dependency: Window, position: MutableObservableProperty<(GeoCoordinate | null)>, zoomLevel?: number, animate?: boolean, style?: string | null): void;
export {};

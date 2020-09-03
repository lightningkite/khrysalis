import {GeoCoordinate} from "khrysalis/dist/location/GeoCoordinate.shared"
import {GeoAddress} from "khrysalis/dist/location/GeoAddress.shared"
import {setGeocodingMethod} from "khrysalis/dist/location/Geocoding.actual"
import {from, Observable, of} from "rxjs";
import { HttpClient} from "khrysalis/dist/net/HttpClient.actual"
import {flatMap, map} from "rxjs/operators";
import { xStringSubstringBefore } from "khrysalis/dist/kotlin/kotlin.text"
import {setMapConfiguration} from "./MapView.bind.actual";
import {tileLayer} from "leaflet";

export function setupMapBox(accessToken: string){

    setMapConfiguration(
        (map, styleString) => {
            tileLayer('https://api.mapbox.com/styles/v1/{id}/tiles/{z}/{x}/{y}?access_token={accessToken}', {
                attribution: '© <a href="https://www.mapbox.com/about/maps/">Mapbox</a> © <a href="http://www.openstreetmap.org/copyright">OpenStreetMap</a> <strong><a href="https://www.mapbox.com/map-feedback/" target="_blank">Improve this map</a></strong>',
                tileSize: 512,
                maxZoom: 18,
                zoomOffset: -1,
                id: 'mapbox/satellite-streets-v11',
                accessToken: accessToken
            }).addTo(map);
        }
    )

    setGeocodingMethod((
        this_Geocode: Window,
        coordinate: GeoCoordinate | string,
        maxResults: number = 1
    ):Observable<Array<GeoAddress>> => {
        let call: Observable<Response>
        if(coordinate instanceof GeoCoordinate){
            call = from(HttpClient.INSTANCE.call(
                `https://api.mapbox.com/geocoding/v5/mapbox.places/${coordinate.longitude},${coordinate.latitude}.json?access_token=${accessToken}&limit=${maxResults}`,
                HttpClient.INSTANCE.GET
            ))
        } else {
            if(coordinate === "") {
                return of([])
            }
            call = from(HttpClient.INSTANCE.call(
                `https://api.mapbox.com/geocoding/v5/mapbox.places/${coordinate}.json?access_token=${accessToken}&limit=${maxResults}`,
                HttpClient.INSTANCE.GET
            ))
        }
        return call.pipe(
            flatMap((raw: Response)=>{
                return raw.json()
            }),
            map((json: any) => {
                return (json.features as Array<any>).map((x) => {
                    const extractedContext: Map<string, string> = new Map((x.context as Array<any>).map((y) => {
                        return [xStringSubstringBefore(y.id, ".", undefined), y.text as string]
                    }));
                    extractedContext.set(xStringSubstringBefore(x.id, ".", undefined), x.text as string)
                    return new GeoAddress(
                        /*coordinate*/ new GeoCoordinate(x.center[1], x.center[0]),
                        /*name*/ x.matching_place_name,
                        /*street*/ x.address,
                        /*subLocality*/ extractedContext.get("neighborhood"),
                        /*locality*/ extractedContext.get("place"),
                        /*subAdminArea*/ undefined,
                        /*adminArea*/ extractedContext.get("region"),
                        /*countryName*/ extractedContext.get("country"),
                        /*postalCode*/ extractedContext.get("postcode")
                    )
                })
            })
        )
    });
}
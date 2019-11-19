package com.lightningkite.kwift.location.actual

import android.location.Address
import android.location.Geocoder
import com.lightningkite.kwift.actual.delay
import com.lightningkite.kwift.actual.post
import com.lightningkite.kwift.location.shared.GeoCoordinate
import com.lightningkite.kwift.views.actual.ViewDependency

fun ViewDependency.geocode(
    coordinate: GeoCoordinate,
    onResult: (List<GeoAddress>) -> Unit
) {
    if(coordinate.latitude == 0.0 && coordinate.longitude == 0.0){
        onResult(listOf())
        return
    }
    Thread {
        val result = Geocoder(context)
            .getFromLocation(coordinate.latitude, coordinate.longitude, 1)
        post {
            onResult(result)
        }
    }.start()
}

fun ViewDependency.geocode(
    address: String,
    onResult: (List<GeoAddress>) -> Unit
) {
    if(address.isEmpty()){
        onResult(listOf())
        return
    }
    Thread {
        val result = Geocoder(context)
            .getFromLocationName(address, 1)
        post {
            onResult(result)
        }
    }.start()
}


//fun ViewDependency.geocodeSuggestions(
//    address: String,
//    withCountry: Boolean = false,
//    withZip: Boolean = false,
//    count: Int = 3,
//    onResult: (List<GeocodingSuggestion>) -> Unit
//) {
//    Thread {
//        val results = Geocoder(context)
//            .getFromLocationName(address, count)
//            .map {
//                GeocodingSuggestion(
//                    name = it.toOneLineString(withCountry, withZip),
//                    coordinate = GeoCoordinate(it.latitude, it.longitude)
//                )
//            }
//        post {
//            onResult(results)
//        }
//    }.start()
//}

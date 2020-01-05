package com.lightningkite.kwift.location

import android.location.Address
import android.location.Geocoder
import com.lightningkite.kwift.delay
import com.lightningkite.kwift.post
import com.lightningkite.kwift.location.GeoCoordinate
import com.lightningkite.kwift.views.ViewDependency

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

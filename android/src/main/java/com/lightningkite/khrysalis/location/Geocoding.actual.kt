package com.lightningkite.khrysalis.location

import android.location.Address
import android.location.Geocoder
import com.lightningkite.khrysalis.delay
import com.lightningkite.khrysalis.post
import com.lightningkite.khrysalis.location.GeoCoordinate
import com.lightningkite.khrysalis.views.ViewDependency

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

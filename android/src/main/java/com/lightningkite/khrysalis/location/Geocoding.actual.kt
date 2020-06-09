package com.lightningkite.khrysalis.location

import android.location.Geocoder
import com.lightningkite.khrysalis.post
import com.lightningkite.khrysalis.views.DialogRequest
import com.lightningkite.khrysalis.views.ViewDependency
import com.lightningkite.khrysalis.views.ViewStringRaw
import com.lightningkite.khrysalis.views.showDialog
import io.reactivex.Single

@Deprecated("Use the new RX Style instead")
fun ViewDependency.geocode(
    coordinate: GeoCoordinate,
    onResult: (List<GeoAddress>) -> Unit
) {
    if (coordinate.latitude == 0.0 && coordinate.longitude == 0.0) {
        onResult(listOf())
        return
    }
    Thread {
        try {
            val result = Geocoder(context)
                .getFromLocation(coordinate.latitude, coordinate.longitude, 1)
            post {
                onResult(result.map { it -> it.toKhrysalis() })
            }
        } catch (e: Exception) {
            showDialog(DialogRequest(string = ViewStringRaw(e.message ?: "An unknown error occured")))
        }
    }.start()
}

@Deprecated("Use the new RX Style instead")
fun ViewDependency.geocode(
    address: String,
    onResult: (List<GeoAddress>) -> Unit
) {
    if (address.isEmpty()) {
        onResult(listOf())
        return
    }
    Thread {
        try {
            val result = Geocoder(context)
                .getFromLocationName(address, 1)
            post {
                onResult(result.map { it -> it.toKhrysalis() })
            }
        } catch (e: Exception) {
            showDialog(ViewStringRaw(e.message ?: "An unknown error occured"))
        }
    }.start()
}

fun ViewDependency.geocode(
    address: String,
    maxResults: Int = 1
): Single<List<GeoAddress>> {
    if (address.isEmpty()) {
        return Single.just(listOf())
    }
    return Single.create { emitter ->
        Thread {
            try {
                emitter.onSuccess(Geocoder(context)
                    .getFromLocationName(address, maxResults).map { it -> it.toKhrysalis() })
            } catch (e: Exception) {
                emitter.onError(e)
            }
        }.start()
    }
}

fun ViewDependency.geocode(
    coordinate: GeoCoordinate,
    maxResults: Int = 1
): Single<List<GeoAddress>> {
    if (coordinate.latitude == 0.0 && coordinate.longitude == 0.0) {
        return Single.just(listOf())
    }
    return Single.create { emitter ->
        Thread {
            try {
                emitter.onSuccess(Geocoder(context)
                    .getFromLocation(coordinate.latitude, coordinate.longitude, maxResults).map { it -> it.toKhrysalis() })

            } catch (e: Exception) {
                emitter.onError(e)
            }
        }.start()
    }
}

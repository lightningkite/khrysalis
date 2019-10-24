package com.lightningkite.kwift.views.actual

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.location.Criteria
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import com.lightningkite.kwift.shared.LocationResult
import java.util.*
import java.util.concurrent.atomic.AtomicBoolean

@SuppressLint("MissingPermission")
fun ViewDependency.requestLocation(
    accuracyBetterThanMeters: Double = 10.0,
    timeoutInSeconds: Double = 100.0,
    onResult: (LocationResult?, String?) -> Unit
) {
    val alreadyDone = AtomicBoolean(false)
    val manager = (context.getSystemService(Context.LOCATION_SERVICE) as? LocationManager)
    val listener = object : LocationListener {
        override fun onLocationChanged(location: Location) {
            if (alreadyDone.compareAndSet(false, true))
                onResult(
                    LocationResult(
                        latitude = location.latitude,
                        longitude = location.longitude,
                        accuracyMeters = location.accuracy.toDouble(),
                        altitudeMeters = location.altitude,
                        altitudeAccuracyMeters = 100.0,
                        headingFromNorth = location.bearing.toDouble(),
                        speedMetersPerSecond = location.speed.toDouble()
                    ),
                    null
                )
        }

        override fun onStatusChanged(p0: String?, p1: Int, p2: Bundle?) {

        }

        override fun onProviderEnabled(p0: String?) {

        }

        override fun onProviderDisabled(p0: String?) {

        }
    }
    requestPermission(Manifest.permission.ACCESS_FINE_LOCATION) {
        if (it) {
            val criteria = Criteria()
            criteria.horizontalAccuracy = when (accuracyBetterThanMeters) {
                in 0f..100f -> Criteria.ACCURACY_HIGH
                in 100f..500f -> Criteria.ACCURACY_MEDIUM
                else -> Criteria.ACCURACY_LOW
            }
            manager?.requestSingleUpdate(
                Criteria(),
                listener,
                Looper.getMainLooper()
            )
        } else {
            if (alreadyDone.compareAndSet(false, true))
                onResult(null, "No permission")
        }
    }
    Handler(Looper.getMainLooper()).postDelayed({
        try {
            manager?.removeUpdates(listener)
            if (alreadyDone.compareAndSet(false, true))
                onResult(null, "Timeout")
        } catch (e: Exception) {
            e.printStackTrace()
            //squish
        }
    }, timeoutInSeconds.times(1000).toLong())
}


data class LocationCache(var location: LocationResult, var timeSinceCall: Date, var accuracy: Double)

var lastLocation: LocationCache? = null

fun ViewDependency.requestLocationCached(
    accuracyBetterThanMeters: Double = 10.0,
    timeoutInSeconds: Double = 100.0,
    onResult: (LocationResult?, String?) -> Unit
) {
    if (lastLocation != null && lastLocation!!.timeSinceCall.time - java.util.Date().time < 300000 && lastLocation!!.accuracy < accuracyBetterThanMeters) {
        onResult(
            lastLocation!!.location,
            null
        )
    } else {
        requestLocation(accuracyBetterThanMeters) { result, string ->
            result?.let { it ->
                lastLocation = LocationCache(it, Date(), accuracyBetterThanMeters)
            }
            onResult(result, string)
        }
    }
}
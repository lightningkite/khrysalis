package com.lightningkite.kwift.views.actual

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.location.Criteria
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.util.Log
import androidx.core.content.FileProvider
import com.lightningkite.kwift.actual.ImageData
import com.lightningkite.kwift.actual.ImageReference
import com.lightningkite.kwift.android.ActivityAccess
import com.lightningkite.kwift.shared.LocationResult
import com.lightningkite.kwift.views.android.startIntent
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import java.io.File
import java.lang.Exception
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

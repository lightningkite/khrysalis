package com.lightningkite.khrysalis.location

import android.location.Location
import com.lightningkite.khrysalis.Codable

data class GeoCoordinate(val latitude: Double, val longitude: Double): Codable

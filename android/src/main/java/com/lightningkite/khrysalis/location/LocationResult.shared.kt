package com.lightningkite.khrysalis.location



data class LocationResult(
    val coordinate: GeoCoordinate = GeoCoordinate(0.0, 0.0),
    val accuracyMeters: Double = 100.0,
    val altitudeMeters: Double = 0.0,
    val altitudeAccuracyMeters: Double = 100.0,
    val headingFromNorth: Double = 0.0, //Degrees
    val speedMetersPerSecond: Double = 0.0
)

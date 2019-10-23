package com.lightningkite.kwift.shared



data class LocationResult(
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val accuracyMeters: Double = 100.0,
    val altitudeMeters: Double = 0.0,
    val altitudeAccuracyMeters: Double = 100.0,
    val headingFromNorth: Double = 0.0, //Degrees
    val speedMetersPerSecond: Double = 0.0
)

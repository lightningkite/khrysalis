package com.lightningkite.kwift.location.actual

import android.location.Address
import com.lightningkite.kwift.location.shared.GeoCoordinate
import java.util.*

typealias GeoAddress = Address

fun GeoAddress(): GeoAddress = GeoAddress(Locale.getDefault())

val GeoAddress.street: String
    get() {
        return (0..maxAddressLineIndex).joinToString(" ") { getAddressLine(it) }
    }

fun GeoAddress.oneLine(withCountry: Boolean = false, withZip: Boolean = false): String = buildString {
//    this@oneLine.featureName?.let {
//        append(it)
//        append(' ')
//    } ?: run {
        for (line in 0..maxAddressLineIndex) {
            append(getAddressLine(line))
            append(' ')
        }
//    }
    this@oneLine.locality?.let {
        append(it)
    }
    this@oneLine.adminArea?.let {
        append(", ")
        append(it)
    }
    if (withCountry) {
        this@oneLine.adminArea?.let {
            append(' ')
            append(it)
        }
    }
    if (withZip) {
        this@oneLine.postalCode?.let {
            append(' ')
            append(it)
        }
    }
}.trim()

val GeoAddress.coordinate: GeoCoordinate? get() = if(hasLatitude()) GeoCoordinate(latitude, longitude) else null


//name
//street
//locality
//adminArea
//countryName

//coordinate

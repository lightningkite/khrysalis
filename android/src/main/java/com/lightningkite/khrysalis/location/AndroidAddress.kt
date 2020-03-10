package com.lightningkite.khrysalis.location

import android.location.Address

fun Address.toKhrysalis(): GeoAddress = GeoAddress(
    coordinate = coordinate,
    name = null,
    street = street,
    subLocality = subLocality,
    locality = locality,
    adminArea = adminArea,
    countryName = countryName,
    postalCode = postalCode
)

val Address.street: String?
    get() {
        return subThoroughfare?.let { x ->
            thoroughfare?.let { y ->
                "$x $y"
            }
        }
    }

val Address.oneLineShort: String
    get() = oneLine()

fun Address.oneLine(withCountry: Boolean = false, withZip: Boolean = false): String = buildString {
    street?.let {
        append(it)
    }
    this@oneLine.locality?.let {
        append(' ')
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

val Address.coordinate: GeoCoordinate? get() = if (hasLatitude()) GeoCoordinate(latitude, longitude) else null


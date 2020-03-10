package com.lightningkite.khrysalis.location

import com.lightningkite.khrysalis.Codable


data class GeoAddress(
    val coordinate: GeoCoordinate? = null,
    val name: String? = null,
    val street: String? = null,
    val subLocality: String? = null,
    val locality: String? = null,
    val subAdminArea: String? = null,
    val adminArea: String? = null,
    val countryName: String? = null,
    val postalCode: String? = null
): Codable {
    fun oneLine(withCountry: Boolean = false, withZip: Boolean = false): String {
        val builder = StringBuilder()
        street?.let {
            builder.append(it)
        }
        locality?.let {
            builder.append(' ')
            builder.append(it)
        }
        adminArea?.let {
            builder.append(", ")
            builder.append(it)
        }
        if (withCountry) {
            adminArea?.let {
                builder.append(' ')
                builder.append(it)
            }
        }
        if (withZip) {
            postalCode?.let {
                builder.append(' ')
                builder.append(it)
            }
        }
        return builder.toString().trim()
    }
}

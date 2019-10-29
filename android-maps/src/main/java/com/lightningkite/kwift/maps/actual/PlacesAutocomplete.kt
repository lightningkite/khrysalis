package com.lightningkite.kwift.maps.actual

import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.AutocompleteSessionToken
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.android.libraries.places.api.net.PlacesClient
import com.lightningkite.kwift.location.actual.GeoAddress
import com.lightningkite.kwift.views.actual.ViewDependency
import java.util.*

class PlacesAutocomplete(dependency: ViewDependency){
    private val client = Places.createClient(dependency.context)
    private val token: AutocompleteSessionToken = AutocompleteSessionToken.newInstance()
    private fun buildRequest(query: String) = FindAutocompletePredictionsRequest.builder()
        .setCountry("us")
        .setSessionToken(token)
        .setQuery(query)
        .build()

    fun request(query: String, onResult: (List<String>)->Unit) {
        client.findAutocompletePredictions(buildRequest(query))
            .addOnSuccessListener { result ->
                onResult(result.autocompletePredictions.map { it.getFullText(null).toString() })
            }
            .addOnFailureListener {
                it.printStackTrace()
                onResult(listOf())
            }
    }
}

package com.lightningkite.khrysalis.maps

import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.AutocompleteSessionToken
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.lightningkite.khrysalis.location.GeoAddress
import com.lightningkite.khrysalis.views.ViewDependency

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

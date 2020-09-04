@file:Suppress("NAME_SHADOWING")

package com.lightningkite.khrysalis.maps

import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.AutocompletePrediction
import com.google.android.libraries.places.api.model.AutocompleteSessionToken
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.model.TypeFilter
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.api.net.FetchPlaceResponse
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.lightningkite.khrysalis.observables.ObservableProperty
import com.lightningkite.khrysalis.observables.observableNN
import com.lightningkite.khrysalis.observables.subscribeBy
import com.lightningkite.khrysalis.post
import com.lightningkite.khrysalis.rx.DisposeCondition
import com.lightningkite.khrysalis.rx.forever
import com.lightningkite.khrysalis.rx.until
import com.lightningkite.khrysalis.views.ViewDependency
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.subjects.PublishSubject
import java.util.*
import java.util.concurrent.TimeUnit

class PlacesAutocomplete(dependency: ViewDependency) {
    private val client = Places.createClient(dependency.context)
    private var token: AutocompleteSessionToken? = AutocompleteSessionToken.newInstance()
    private var working = false
    private var cachedRequest: List<AutocompletePrediction> = listOf()
    private val detailFields = listOf(
        Place.Field.ID,
        Place.Field.LAT_LNG,
        Place.Field.NAME,
        Place.Field.ADDRESS_COMPONENTS,
        Place.Field.ADDRESS
    )

    private fun buildRequest(query: String, filter: TypeFilter? = null): FindAutocompletePredictionsRequest {
        if (token == null) {
            token = AutocompleteSessionToken.newInstance()
        }
        return FindAutocompletePredictionsRequest.builder()
            .setCountry("US")
            .setSessionToken(token)
            .setTypeFilter(filter)
            .setQuery(query)
            .build()
    }

    fun request(query: String, filter: TypeFilter? = null): Single<List<AutocompletePrediction>> {
        return Single.create { emitter ->
            if (working) {
                emitter.onSuccess(cachedRequest)
            } else {
                working = true
                client.findAutocompletePredictions(buildRequest(query, filter))
                    .addOnSuccessListener { results ->
                        working = false
                        cachedRequest = results.autocompletePredictions
                        post {
                            emitter.onSuccess(cachedRequest)
                        }
                    }
                    .addOnFailureListener {
                        working = false
                        post {
                            emitter.onError(it)
                        }
                    }
            }
        }
    }

    fun request(
        query: ObservableProperty<String>,
        disposeCondition: DisposeCondition,
        filter: TypeFilter? = null
    ): Observable<List<AutocompletePrediction>> {
        val subject = PublishSubject.create<List<AutocompletePrediction>>()
        query
            .observableNN
            .debounce(750L, TimeUnit.MILLISECONDS)
            .subscribeBy { query ->
                if (!working) {
                    working = true
                    client.findAutocompletePredictions(buildRequest(query, filter))
                        .addOnSuccessListener { results ->
                            working = false
                            post {
                                subject.onNext(results.autocompletePredictions)
                            }
                        }
                        .addOnFailureListener {
                            working = false
                        }
                }
            }.until(disposeCondition)

        return subject
    }

    fun details(id: String, details: List<Place.Field>? = null): Single<Place> {
        Place.Field.ADDRESS
        return Single.create { emitter ->
            working = true
            val request = FetchPlaceRequest.builder(id, details ?: detailFields)
                .setSessionToken(token)
                .build()
            client.fetchPlace(request)
                .addOnSuccessListener { response ->
                    token = null
                    post {
                        emitter.onSuccess(response.place)
                    }
                    working = false
                }
                .addOnFailureListener { exception ->
                    token = null
                    post {
                        emitter.onError(exception)
                    }
                    working = false
                }
        }
    }
}

val AutocompletePrediction.types:List<String>
    get() = this.placeTypes.map { it.name }

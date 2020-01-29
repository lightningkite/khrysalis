package com.lightningkite.khrysalis.maps

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.lightningkite.khrysalis.location.GeoCoordinate
import com.lightningkite.khrysalis.observables.MutableObservableProperty
import com.lightningkite.khrysalis.observables.ObservableProperty
import com.lightningkite.khrysalis.observables.addAndRunWeak
import com.lightningkite.khrysalis.rx.addWeak
import com.lightningkite.khrysalis.views.ViewDependency

fun MapView.bind(dependency: ViewDependency) {
    this.onCreate(dependency.savedInstanceState)
    this.onResume()
    dependency.onResume.addWeak(this) { self, value ->
        self.onResume()
    }
    dependency.onPause.addWeak(this) { self, value ->
        self.onPause()
    }
    dependency.onSaveInstanceState.addWeak(this) { self, value ->
        self.onSaveInstanceState(value)
    }
    dependency.onLowMemory.addWeak(this) { self, value ->
        self.onLowMemory()
    }
    dependency.onDestroy.addWeak(this) { self, value ->
        self.onDestroy()
    }
}

fun MapView.bindView(
    dependency: ViewDependency,
    position: ObservableProperty<GeoCoordinate?>,
    zoomLevel: Float = 15f,
    animate: Boolean = true
) {
    bind(dependency)
    getMapAsync { map ->
        var marker: Marker? = null
        @Suppress("NAME_SHADOWING")
        position.addAndRunWeak(this) { view, value ->
            if (value != null) {
                val newMarker = marker ?: map.addMarker(MarkerOptions().draggable(true).position(value.toMaps()))
                newMarker.position = value.toMaps()
                marker = newMarker
                if (animate) {
                    map.animateCamera(CameraUpdateFactory.newLatLngZoom(value.toMaps(), zoomLevel))
                } else {
                    map.moveCamera(CameraUpdateFactory.newLatLngZoom(value.toMaps(), zoomLevel))
                }
            } else {
                marker?.remove()
                marker = null
            }
        }
    }
}


fun MapView.bindSelect(
    dependency: ViewDependency,
    position: MutableObservableProperty<GeoCoordinate?>,
    zoomLevel: Float = 15f,
    animate: Boolean = true
) {
    bind(dependency)
    getMapAsync { map ->
        var suppress: Boolean = false
        var suppressAnimation: Boolean = false
        var marker: Marker? = null
        @Suppress("NAME_SHADOWING")
        position.addAndRunWeak(this) { view, value ->
            if (!suppress) {
                suppress = true
                if (value != null) {
                    val newMarker = marker ?: map.addMarker(MarkerOptions().draggable(true).position(value.toMaps()))
                    newMarker.position = value.toMaps()
                    marker = newMarker
                    if (!suppressAnimation) {
                        if (animate) {
                            map.animateCamera(CameraUpdateFactory.newLatLngZoom(value.toMaps(), zoomLevel))
                        } else {
                            map.moveCamera(CameraUpdateFactory.newLatLngZoom(value.toMaps(), zoomLevel))
                        }
                    }
                } else {
                    marker?.remove()
                    marker = null
                }
                suppress = false
            }
        }

        map.setOnMapLongClickListener { coord ->
            suppressAnimation = true
            position.value = coord.toKhrysalis()
            suppressAnimation = false
        }
        map.setOnMarkerDragListener(object : GoogleMap.OnMarkerDragListener {
            override fun onMarkerDragEnd(marker: Marker) {
                if (!suppress) {
                    suppress = true
                    position.value = marker.position.toKhrysalis()
                    suppress = false
                }
            }

            override fun onMarkerDragStart(p0: Marker) {}
            override fun onMarkerDrag(p0: Marker) {}
        })
    }
}

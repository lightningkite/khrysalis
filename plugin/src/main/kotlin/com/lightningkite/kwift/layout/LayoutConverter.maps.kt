package com.lightningkite.kwift.layout

import com.lightningkite.kwift.utils.*
import kotlin.math.PI

val LayoutConverter.Companion.mapViews
    get() = LayoutConverter(
        imports = setOf("MapKit"),
        viewTypes = ViewType.mapOf(
            ViewType("com.google.android.gms.maps.MapView", "MKMapView", "View") { node ->

            }
        )
    )

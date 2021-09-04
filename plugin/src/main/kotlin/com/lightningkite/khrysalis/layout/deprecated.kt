package com.lightningkite.khrysalis.layout

import com.lightningkite.khrysalis.ios.layout.*
import com.lightningkite.khrysalis.ios.layout.LayoutConverter
import com.lightningkite.khrysalis.utils.attributeAsBoolean

@Deprecated("Use the newly packaged one")
typealias LayoutConverter = LayoutConverter

@Deprecated("Use the newly packaged one")
val LayoutConverter.Companion.normal get() = LayoutConverter(
    LayoutConverter.buttonViews,
    LayoutConverter.dateViews,
    LayoutConverter.displayViews,
    LayoutConverter.layoutViews,
    LayoutConverter.navigationViews,
    LayoutConverter.textInputViews
)

@Deprecated("Use the newly packaged one")
val LayoutConverter.Companion.mapViews get() = LayoutConverter(
    imports = setOf("MapKit"),
    viewTypes = ViewType.mapOf(
        ViewType("com.google.android.gms.maps.MapView", "MKMapView", "View") { node ->
            node.attributeAsBoolean("app:liteMode")?.let {
                if (it) {
                    appendLine("view.isZoomEnabled = false")
                    appendLine("view.isScrollEnabled = false")
                    appendLine("view.isUserInteractionEnabled = false")
                }
            }
        }
    )
)


package com.lightningkite.khrysalis.ios.layout

import com.lightningkite.khrysalis.utils.attributeAsBoolean

val LayoutConverter.Companion.mapViews
    get() = LayoutConverter(
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

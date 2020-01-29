package com.lightningkite.khrysalis.layout

import com.lightningkite.khrysalis.utils.attributeAsBoolean

val LayoutConverter.Companion.mapViews
    get() = LayoutConverter(
        imports = setOf("MapKit"),
        viewTypes = ViewType.mapOf(
            ViewType("com.google.android.gms.maps.MapView", "MKMapView", "View") { node ->
                node.attributeAsBoolean("app:liteMode")?.let {
                    if(it){
                        appendln("view.isZoomEnabled = false")
                        appendln("view.isScrollEnabled = false")
                        appendln("view.isUserInteractionEnabled = false")
                    }
                }
            }
        )
    )

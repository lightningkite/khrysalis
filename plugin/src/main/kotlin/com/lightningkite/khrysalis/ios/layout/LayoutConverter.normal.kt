package com.lightningkite.khrysalis.ios.layout

val LayoutConverter.Companion.normal
    get() = LayoutConverter(
        LayoutConverter.buttonViews,
        LayoutConverter.dateViews,
        LayoutConverter.displayViews,
        LayoutConverter.layoutViews,
        LayoutConverter.navigationViews,
        LayoutConverter.mapViews,
        LayoutConverter.qrViews,
        LayoutConverter.textInputViews
    )

package com.lightningkite.khrysalis.layout

val LayoutConverter.Companion.normal
    get() = LayoutConverter(
        LayoutConverter.buttonViews,
        LayoutConverter.dateViews,
        LayoutConverter.displayViews,
        LayoutConverter.layoutViews,
        LayoutConverter.navigationViews,
        LayoutConverter.textInputViews
    )

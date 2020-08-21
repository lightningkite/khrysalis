package com.lightningkite.khrysalis.ios.layout

import com.lightningkite.khrysalis.utils.attributeAsBoolean

val LayoutConverter.Companion.qrViews
    get() = LayoutConverter(
        imports = setOf("KhrysalisQR"),
        viewTypes = ViewType.mapOf(
            ViewType("com.lightningkite.khrysalis.qr.BarcodeScannerView", "BarcodeScannerView", "View"){}
        )
    )

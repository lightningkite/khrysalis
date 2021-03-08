package com.lightningkite.khrysalis.ios.layout

import com.lightningkite.khrysalis.utils.attributeAsBoolean

val LayoutConverter.Companion.qrViews
    get() = LayoutConverter(
        imports = setOf("LKButterflyQR"),
        viewTypes = ViewType.mapOf(
            ViewType("com.lightningkite.butterfly.qr.BarcodeScannerView", "BarcodeScannerView", "View"){}
        )
    )

package com.lightningkite.khrysalis.ios.layout

import com.lightningkite.khrysalis.utils.*
import com.lightningkite.khrysalis.ios.*

val LayoutConverter.Companion.dateViews
    get() = LayoutConverter(
        viewTypes = ViewType.mapOf(
            ViewType("com.lightningkite.khrysalis.views.android.TimeButton", "TimeButton", "Button", handlesPadding = true) {},
            ViewType("com.lightningkite.khrysalis.views.android.DateButton", "DateButton", "Button", handlesPadding = true) {},
            ViewType("com.lightningkite.khrysalis.views.android.WeekView", "UIWeekView", "View") { node ->
                //TODO
            }
        )
    )

package com.lightningkite.khrysalis.ios.layout

import com.lightningkite.khrysalis.utils.*
import com.lightningkite.khrysalis.ios.*

val LayoutConverter.Companion.dateViews
    get() = LayoutConverter(
        viewTypes = ViewType.mapOf(
            ViewType("com.lightningkite.butterfly.views.TimeButton", "TimeButton", "Button", handlesPadding = true) {},
            ViewType("com.lightningkite.butterfly.views.DateButton", "DateButton", "Button", handlesPadding = true) {},
            ViewType("com.lightningkite.butterfly.views.WeekView", "UIWeekView", "View") { node ->
                //TODO
            }
        )
    )

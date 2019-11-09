package com.lightningkite.kwift.layout

import com.lightningkite.kwift.utils.attributeAsColor
import com.lightningkite.kwift.utils.attributeAsDimension
import com.lightningkite.kwift.utils.attributeAsString

val LayoutConverter.Companion.dateViews
    get() = LayoutConverter(
        viewTypes = ViewType.mapOf(
            ViewType("com.lightningkite.kwift.views.android.TimeButton", "TimeButton", "Button") {},
            ViewType("com.lightningkite.kwift.views.android.DateButton", "DateButton", "Button") {},
            ViewType("com.lightningkite.kwift.views.android.WeekView", "UIWeekView", "View") { node ->
                //TODO
            },
            ViewType("com.lightningkite.kwift.views.android.CalendarView", "CalendarView", "View") { node ->

                node.attributeAsColor("app:selectedForegroundColor")?.let {
                    appendln("view.selectedColorSet.foreground = $it")
                }
                node.attributeAsColor("app:selectedBackgroundColor")?.let {
                    appendln("view.selectedColorSet.background = $it")
                }
                node.attributeAsColor("app:defaultForegroundColor")?.let {
                    appendln("view.defaultColorSet.foreground = $it")
                }
                node.attributeAsColor("app:defaultBackgroundColor")?.let {
                    appendln("view.defaultColorSet.background = $it")
                }
                node.attributeAsColor("app:labelForegroundColor")?.let {
                    appendln("view.labelColorSet.foreground = $it")
                }
                node.attributeAsColor("app:labelBackgroundColor")?.let {
                    appendln("view.labelColorSet.background = $it")
                }

                node.attributeAsDimension("app:headerFont")?.let {
                    appendln("view.headerFont = $it")
                }
                node.attributeAsDimension("app:labelFont")?.let {
                    appendln("view.labelFont = $it")
                }
                node.attributeAsDimension("app:dayFont")?.let {
                    appendln("view.dayFont = $it")
                }

                node.attributeAsDimension("app:internalPadding")?.let {
                    appendln("view.internalPadding = $it")
                }
                node.attributeAsDimension("app:dayCellMargin")?.let {
                    appendln("view.dayCellMargin = $it")
                }

                node.attributeAsString("app:leftText")?.let {
                    appendln("view.leftText = $it")
                }
                node.attributeAsString("app:rightText")?.let {
                    appendln("view.rightText = $it")
                }
                node.attributeAsColor("selectedForegroundColor")?.let {
                    appendln("view.selectedColorSet.foreground = $it")
                }
                node.attributeAsColor("selectedBackgroundColor")?.let {
                    appendln("view.selectedColorSet.background = $it")
                }
                node.attributeAsColor("defaultForegroundColor")?.let {
                    appendln("view.defaultColorSet.foreground = $it")
                }
                node.attributeAsColor("defaultBackgroundColor")?.let {
                    appendln("view.defaultColorSet.background = $it")
                }
                node.attributeAsColor("labelForegroundColor")?.let {
                    appendln("view.labelColorSet.foreground = $it")
                }
                node.attributeAsColor("labelBackgroundColor")?.let {
                    appendln("view.labelColorSet.background = $it")
                }

                node.attributeAsDimension("headerFont")?.let {
                    appendln("view.headerFont = $it")
                }
                node.attributeAsDimension("labelFont")?.let {
                    appendln("view.labelFont = $it")
                }
                node.attributeAsDimension("dayFont")?.let {
                    appendln("view.dayFont = $it")
                }

                node.attributeAsDimension("internalPadding")?.let {
                    appendln("view.internalPadding = $it")
                }
                node.attributeAsDimension("dayCellMargin")?.let {
                    appendln("view.dayCellMargin = $it")
                }

                node.attributeAsString("leftText")?.let {
                    appendln("view.leftText = $it")
                }
                node.attributeAsString("rightText")?.let {
                    appendln("view.rightText = $it")
                }
            },
            ViewType(
                "com.lightningkite.kwift.views.android.SelectDateRangeView",
                "SelectDateRangeView",
                "com.lightningkite.kwift.views.android.CalendarView"
            ) { node ->

            },
            ViewType(
                "com.lightningkite.kwift.views.android.SelectDayView",
                "SelectDayView",
                "com.lightningkite.kwift.views.android.CalendarView"
            ) { node ->

            },
            ViewType(
                "com.lightningkite.kwift.views.android.SelectMultipleDatesView",
                "SelectMultipleDatesView",
                "com.lightningkite.kwift.views.android.CalendarView"
            ) { node ->

            }
        )
    )

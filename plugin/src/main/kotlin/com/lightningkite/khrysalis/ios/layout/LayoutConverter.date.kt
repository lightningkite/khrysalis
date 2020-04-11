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
            },
            ViewType("com.lightningkite.khrysalis.views.android.CalendarView", "CalendarView", "View") { node ->

                node.attributeAsSwiftColor("app:titleColor")?.let {
                    appendln("view.headerColorSet.foreground = $it")
                }
                node.attributeAsSwiftColor("app:selectedForegroundColor")?.let {
                    appendln("view.selectedColorSet.foreground = $it")
                }
                node.attributeAsSwiftColor("app:selectedBackgroundColor")?.let {
                    appendln("view.selectedColorSet.background = $it")
                }
                node.attributeAsSwiftColor("app:defaultForegroundColor")?.let {
                    appendln("view.defaultColorSet.foreground = $it")
                }
                node.attributeAsSwiftColor("app:defaultBackgroundColor")?.let {
                    appendln("view.defaultColorSet.background = $it")
                }
                node.attributeAsSwiftColor("app:labelForegroundColor")?.let {
                    appendln("view.labelColorSet.foreground = $it")
                }
                node.attributeAsSwiftColor("app:labelBackgroundColor")?.let {
                    appendln("view.labelColorSet.background = $it")
                }

                node.attributeAsSwiftDimension("app:headerFont")?.let {
                    appendln("view.headerFont = $it")
                }
                node.attributeAsSwiftDimension("app:labelFont")?.let {
                    appendln("view.labelFont = $it")
                }
                node.attributeAsSwiftDimension("app:dayFont")?.let {
                    appendln("view.dayFont = $it")
                }

                node.attributeAsSwiftDimension("app:titleSize")?.let {
                    appendln("view.headerFont = $it")
                }
                node.attributeAsSwiftDimension("app:labelSize")?.let {
                    appendln("view.labelFont = $it")
                }
                node.attributeAsSwiftDimension("app:daySize")?.let {
                    appendln("view.dayFont = $it")
                }

                node.attributeAsSwiftDimension("app:internalPadding")?.let {
                    appendln("view.internalPadding = $it")
                }
                node.attributeAsSwiftDimension("app:dayCellMargin")?.let {
                    appendln("view.dayCellMargin = $it")
                }

                node.attributeAsSwiftString("app:leftText")?.let {
                    appendln("view.leftText = $it")
                }
                node.attributeAsSwiftString("app:rightText")?.let {
                    appendln("view.rightText = $it")
                }


                node.attributeAsSwiftColor("titleColor")?.let {
                    appendln("view.headerColorSet.foreground = $it")
                }
                node.attributeAsSwiftColor("selectedForegroundColor")?.let {
                    appendln("view.selectedColorSet.foreground = $it")
                }
                node.attributeAsSwiftColor("selectedBackgroundColor")?.let {
                    appendln("view.selectedColorSet.background = $it")
                }
                node.attributeAsSwiftColor("defaultForegroundColor")?.let {
                    appendln("view.defaultColorSet.foreground = $it")
                }
                node.attributeAsSwiftColor("defaultBackgroundColor")?.let {
                    appendln("view.defaultColorSet.background = $it")
                }
                node.attributeAsSwiftColor("labelForegroundColor")?.let {
                    appendln("view.labelColorSet.foreground = $it")
                }
                node.attributeAsSwiftColor("labelBackgroundColor")?.let {
                    appendln("view.labelColorSet.background = $it")
                }

                node.attributeAsSwiftDimension("headerFont")?.let {
                    appendln("view.headerFont = $it")
                }
                node.attributeAsSwiftDimension("labelFont")?.let {
                    appendln("view.labelFont = $it")
                }
                node.attributeAsSwiftDimension("dayFont")?.let {
                    appendln("view.dayFont = $it")
                }

                node.attributeAsSwiftDimension("titleSize")?.let {
                    appendln("view.headerFont = $it")
                }
                node.attributeAsSwiftDimension("labelSize")?.let {
                    appendln("view.labelFont = $it")
                }
                node.attributeAsSwiftDimension("daySize")?.let {
                    appendln("view.dayFont = $it")
                }

                node.attributeAsSwiftDimension("internalPadding")?.let {
                    appendln("view.internalPadding = $it")
                }
                node.attributeAsSwiftDimension("dayCellMargin")?.let {
                    appendln("view.dayCellMargin = $it")
                }

                node.attributeAsSwiftString("leftText")?.let {
                    appendln("view.leftText = $it")
                }
                node.attributeAsSwiftString("rightText")?.let {
                    appendln("view.rightText = $it")
                }


            },
            ViewType(
                "com.lightningkite.khrysalis.views.android.SelectDateRangeView",
                "SelectDateRangeView",
                "com.lightningkite.khrysalis.views.android.CalendarView"
            ) { node ->

            },
            ViewType(
                "com.lightningkite.khrysalis.views.android.SelectDayView",
                "SelectDayView",
                "com.lightningkite.khrysalis.views.android.CalendarView"
            ) { node ->

            },
            ViewType(
                "com.lightningkite.khrysalis.views.android.SelectMultipleDatesView",
                "SelectMultipleDatesView",
                "com.lightningkite.khrysalis.views.android.CalendarView"
            ) { node ->

            }
        )
    )

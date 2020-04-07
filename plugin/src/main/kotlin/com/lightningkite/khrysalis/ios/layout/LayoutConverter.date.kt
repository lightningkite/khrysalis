package com.lightningkite.khrysalis.ios.layout

import com.lightningkite.khrysalis.utils.attributeAsColor
import com.lightningkite.khrysalis.utils.attributeAsDimension
import com.lightningkite.khrysalis.utils.attributeAsString

val LayoutConverter.Companion.dateViews
    get() = LayoutConverter(
        viewTypes = ViewType.mapOf(
            ViewType("com.lightningkite.khrysalis.views.android.TimeButton", "TimeButton", "Button", handlesPadding = true) {},
            ViewType("com.lightningkite.khrysalis.views.android.DateButton", "DateButton", "Button", handlesPadding = true) {},
            ViewType("com.lightningkite.khrysalis.views.android.WeekView", "UIWeekView", "View") { node ->
                //TODO
            },
            ViewType("com.lightningkite.khrysalis.views.android.CalendarView", "CalendarView", "View") { node ->

                node.attributeAsColor("app:titleColor")?.let {
                    appendln("view.headerColorSet.foreground = $it")
                }
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

                node.attributeAsDimension("app:titleSize")?.let {
                    appendln("view.headerFont = $it")
                }
                node.attributeAsDimension("app:labelSize")?.let {
                    appendln("view.labelFont = $it")
                }
                node.attributeAsDimension("app:daySize")?.let {
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


                node.attributeAsColor("titleColor")?.let {
                    appendln("view.headerColorSet.foreground = $it")
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

                node.attributeAsDimension("titleSize")?.let {
                    appendln("view.headerFont = $it")
                }
                node.attributeAsDimension("labelSize")?.let {
                    appendln("view.labelFont = $it")
                }
                node.attributeAsDimension("daySize")?.let {
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

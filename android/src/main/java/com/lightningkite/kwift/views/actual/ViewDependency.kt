package com.lightningkite.kwift.views.actual

import android.util.DisplayMetrics
import com.lightningkite.kwift.android.ActivityAccess

typealias ViewDependency = ActivityAccess

fun ActivityAccess.getString(resource: StringResource): String = context.getString(resource)
fun ActivityAccess.getColor(resource: ColorResource): Int = context.resources.getColor(resource)
val ActivityAccess.displayMetrics: DisplayMetrics get() = context.resources.displayMetrics

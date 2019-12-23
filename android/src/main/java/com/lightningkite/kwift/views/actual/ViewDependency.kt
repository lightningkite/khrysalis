package com.lightningkite.kwift.views.actual

import com.lightningkite.kwift.android.ActivityAccess

typealias ViewDependency = ActivityAccess

fun ActivityAccess.getString(resource: StringResource): String = context.getString(resource)
fun ActivityAccess.getColor(resource: ColorResource): Int = context.getColor(resource)

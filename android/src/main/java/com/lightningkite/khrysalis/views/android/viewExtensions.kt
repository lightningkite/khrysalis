package com.lightningkite.khrysalis.views.android

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.view.View

val Context.activity: Activity?
    get() {
        return when(this){
            is Activity -> this
            is ContextWrapper -> this.baseContext.activity
            else -> null
        }
    }

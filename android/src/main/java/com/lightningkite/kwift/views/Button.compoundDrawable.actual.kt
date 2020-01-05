package com.lightningkite.kwift.views

import android.graphics.drawable.Drawable
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView

var Button.compoundDrawable: Drawable?
    get() = compoundDrawables.asSequence().filterNotNull().firstOrNull()
    set(value){
        val existing = compoundDrawables
        setCompoundDrawablesWithIntrinsicBounds(
            if(existing[0] != null) value else null,
            if(existing[1] != null) value else null,
            if(existing[2] != null) value else null,
            if(existing[3] != null) value else null
        )
    }

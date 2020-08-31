package com.lightningkite.khrysalis.android

import android.content.Context
import android.view.FocusFinder
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import com.lightningkite.khrysalis.ApplicationAccess

private operator fun View.contains(other: View?): Boolean {
    if(other == null) return false
    if(this == other) return true
    else return this.contains(other.parent as? View)
}

fun runKeyboardUpdate(access: ActivityAccess, root: View? = null, discardingRoot: View? = null) {
    val currentFocus = access.activity?.currentFocus
    var dismissOld = false
    if(currentFocus != null) {
        if(currentFocus.isAttachedToWindow && discardingRoot?.contains(currentFocus) != true){
            //If there is already a view in focus, we'll just leave stuff alone.
        } else {
            //dismiss keyboard if the view's gone
            dismissOld = true
        }
    }
    val keyboardView = (root as? ViewGroup)?.let {
        FocusFinder.getInstance().findNextFocus(it, null, View.FOCUS_FORWARD)
    }
    if(keyboardView != null){
        keyboardView.requestFocus()
        if(keyboardView is EditText){
            val imm = access.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(keyboardView, 0)
            dismissOld = false
        }
    }
    if(dismissOld){
        val imm = access.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        access.activity?.window?.attributes?.token?.let {
            imm.hideSoftInputFromWindow(it, 0)
        }
    }
}
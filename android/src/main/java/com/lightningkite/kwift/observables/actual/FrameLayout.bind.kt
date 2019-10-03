package com.lightningkite.kwift.observables.actual

import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import com.lightningkite.kwift.observables.shared.ObservableStack
import com.lightningkite.kwift.observables.shared.addAndRunWeak
import com.lightningkite.kwift.views.actual.ViewDependency
import com.lightningkite.kwift.views.shared.ViewGenerator


fun FrameLayout.bindStack(dependency: ViewDependency, obs: ObservableStack<ViewGenerator>) {
    var currentData = obs.stack.lastOrNull()
    var currentStackSize = obs.stack.size
    var currentView = currentData?.generate(dependency) ?: View(context)
    addView(
        currentView, FrameLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
    )
    obs.addAndRunWeak(this) { self, datas ->
        post {
            if (currentData == datas.lastOrNull()) return@post

            val oldView = currentView
            val oldStackSize = currentStackSize

            val newView = obs.stack.lastOrNull()?.generate(dependency) ?: View(context)
            addView(
                newView, FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
            )
            val newStackSize = datas.size

            if (oldStackSize > newStackSize) {
                oldView.animate().translationX(width.toFloat())
                newView.translationX = -width.toFloat()
                newView.animate().translationX(0f)
            } else if (oldStackSize < newStackSize) {
                oldView.animate().translationX(-width.toFloat())
                newView.translationX = width.toFloat()
                newView.animate().translationX(0f)
            } else {
                oldView.animate().alpha(0f)
                newView.alpha = 0f
                newView.animate().alpha(1f)
            }
            oldView.animate().withEndAction { removeView(oldView) }

            currentData = datas.lastOrNull()
            currentView = newView
            currentStackSize = newStackSize
        }
    }

}

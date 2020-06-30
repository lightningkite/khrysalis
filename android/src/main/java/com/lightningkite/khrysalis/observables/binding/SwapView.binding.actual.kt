package com.lightningkite.khrysalis.observables.binding

import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import com.lightningkite.khrysalis.observables.*
import com.lightningkite.khrysalis.rx.removed
import com.lightningkite.khrysalis.rx.until
import com.lightningkite.khrysalis.views.ViewDependency
import com.lightningkite.khrysalis.views.android.SwapView
import com.lightningkite.khrysalis.views.ViewGenerator


/**
 *
 * Binds the view in the swap view to the top ViewGenerator in the ObservableStack.
 * Any changes to the top of the stack will manifest in the swap view.
 *
 */

fun SwapView.bindStack(dependency: ViewDependency, obs: ObservableStack<ViewGenerator>) {
    var currentData = obs.stack.lastOrNull()
    var currentStackSize = obs.stack.size
    var currentView = currentData?.generate(dependency) ?: View(context)
    addView(
        currentView, FrameLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
    )
    obs.subscribeBy { datas ->
        post {
            if (currentData == datas.lastOrNull()) return@post

            val oldView = currentView
            val oldStackSize = currentStackSize

            var newView = obs.stack.lastOrNull()?.generate(dependency)
            if (newView == null) {
                newView = View(context)
                visibility = View.GONE
            } else {
                visibility = View.VISIBLE
            }
            addView(
                newView, FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
            )
            val newStackSize = datas.size

            when {
                oldStackSize == 0 -> {
                    oldView.animate().alpha(0f)
                    newView.alpha = 0f
                    newView.animate().alpha(1f)
                }
                oldStackSize > newStackSize -> {
                    oldView.animate().translationX(width.toFloat())
                    newView.translationX = -width.toFloat()
                    newView.animate().translationX(0f)
                }
                oldStackSize < newStackSize -> {
                    oldView.animate().translationX(-width.toFloat())
                    newView.translationX = width.toFloat()
                    newView.animate().translationX(0f)
                }
                else -> {
                    oldView.animate().alpha(0f)
                    newView.alpha = 0f
                    newView.animate().alpha(1f)
                }
            }
            oldView.animate().withEndAction { removeView(oldView) }

            currentData = datas.lastOrNull()
            currentView = newView
            currentStackSize = newStackSize
        }
    }.until(this.removed)

}

/**
 *
 * Binds the view in the swap view to the top ViewGenerator in the ObservableStack.
 * Any changes to the top of the stack will manifest in the swap view.
 *
 */

fun SwapView.bindList(dependency: ViewDependency, vgs: List<ViewGenerator>, index:ObservableProperty<Int>) {
    var currentData = vgs.getOrNull(index.value)
    var currentIndex = index.value
    var currentView = currentData?.generate(dependency) ?: View(context)
    addView(
        currentView, FrameLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
    )
    index.subscribeBy { newIndex ->
        post {
            if (currentIndex == newIndex) return@post

            val oldView = currentView
            val oldIndex = currentIndex

            var newView = vgs.getOrNull(newIndex)?.generate(dependency)
            if (newView == null) {
                newView = View(context)
                visibility = View.GONE
            } else {
                visibility = View.VISIBLE
            }
            addView(
                newView, FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
            )
            val newStackSize = newIndex

            when {
//                oldStackSize == 0 -> {
//                    oldView.animate().alpha(0f)
//                    newView.alpha = 0f
//                    newView.animate().alpha(1f)
//                }
                oldIndex > newStackSize -> {
                    oldView.animate().translationX(width.toFloat())
                    newView.translationX = -width.toFloat()
                    newView.animate().translationX(0f)
                }
                oldIndex < newStackSize -> {
                    oldView.animate().translationX(-width.toFloat())
                    newView.translationX = width.toFloat()
                    newView.animate().translationX(0f)
                }
                else -> {
                    oldView.animate().alpha(0f)
                    newView.alpha = 0f
                    newView.animate().alpha(1f)
                }
            }
            oldView.animate().withEndAction { removeView(oldView) }

            currentData = vgs.getOrNull(newIndex)
            currentView = newView
            currentIndex = newStackSize
        }
    }.until(this.removed)

}

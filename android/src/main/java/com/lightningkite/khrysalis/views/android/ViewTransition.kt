package com.lightningkite.khrysalis.views.android

import android.view.View
import android.view.ViewPropertyAnimator

typealias TransitionAnimation = (View) -> ViewPropertyAnimator

enum class ViewTransition(
    val enterPush: TransitionAnimation,
    val exitPush: TransitionAnimation,
    val enterPop: TransitionAnimation,
    val exitPop: TransitionAnimation
) {
    PUSH_POP(enterPush = { view ->
        view.translationX = view.width.toFloat()
        view.visibility = View.VISIBLE
        view.animate()
            .translationX(0f)
    }, exitPush = { view ->
        view.animate()
            .translationX(-1f * view.width.toFloat())
    }, enterPop = { view ->
        view.translationX = -1f * view.width.toFloat()
        view.animate()
            .translationX(0f)
    }, exitPop = { view ->
        view.animate()
            .translationX(view.width.toFloat())
    }),

    UP_DOWN(enterPush = { view ->
        view.translationY = view.height.toFloat()
        view.visibility = View.VISIBLE
        view.animate()
            .translationY(0f)
    }, exitPush = { view ->
        view.animate()
            .translationY(-1f * view.height.toFloat())
    }, enterPop = { view ->
        view.translationY = -1f * view.height.toFloat()
        view.animate()
            .translationY(0f)
    }, exitPop = { view ->
        view.animate()
            .translationY(view.width.toFloat())
    }),
    FADE_IN_OUT(enterPush = { view ->
        view.alpha = 0f
        view.visibility = View.VISIBLE
        view.animate()
            .alpha(1f)
    }, exitPush = { view ->
        view.animate()
            .alpha(0f)
    }, enterPop = { view ->
        view.alpha = 0f
        view.animate()
            .alpha(1f)
    }, exitPop = { view ->
        view.animate()
            .alpha(0f)
    }),
    NONE(
        enterPush = { view ->
            view.animate()
        }, exitPush = { view ->
            view.animate()
        }, enterPop = { view ->
            view.animate()
        }, exitPop = { view ->
            view.animate()
        }
    )
}
package com.lightningkite.khrysalis.lifecycle

import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import com.lightningkite.khrysalis.Box
import com.lightningkite.khrysalis.boxWrap
import com.lightningkite.khrysalis.R
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.subjects.PublishSubject


/**
 * A lifecycle for a view, that starts when the view is attached and ends when it is detatched.
 */
private class ViewLifecycleListener(val view: View) : Lifecycle(), View.OnAttachStateChangeListener {

    override var value = ViewCompat.isAttachedToWindow(view)
        private set

    val event: PublishSubject<Box<Boolean>> = PublishSubject.create()
    override val onChange: Observable<Box<Boolean>>
        get() = event

    override fun onViewDetachedFromWindow(v: View?) {
        if (!value) {
            println("Broken cycling detected in onViewDetachedFromWindow $view")
            return
        }
        value = false
        event.onNext(boxWrap(value))
    }

    override fun onViewAttachedToWindow(v: View?) {
        if (value) {
            println("Broken cycling detected in onViewAttachedToWindow $view")
            return
        }
        value = true
        event.onNext(boxWrap(value))
    }
}

private fun View.forThisAndAllChildrenRecursive(action: (View) -> Unit) {
    action.invoke(this)
    if (this is ViewGroup) {
        for (i in 0..this.childCount - 1) {
            getChildAt(i).forThisAndAllChildrenRecursive(action)
        }
    }
}

/**
 * Gets this view's lifecycle object for events to connect with.
 */
val View.lifecycle: Lifecycle
    get() {
        val old = getTag(R.id.lifecycle)
        if(old is ViewLifecycleListener) return old
        val listener = ViewLifecycleListener(this)
        addOnAttachStateChangeListener(listener)
        setTag(R.id.lifecycle, listener)
        return listener
    }



fun <T: Disposable> T.untilOff(lifecycle: Lifecycle): T {
    lifecycle.closeWhenOff(this)
    return this
}

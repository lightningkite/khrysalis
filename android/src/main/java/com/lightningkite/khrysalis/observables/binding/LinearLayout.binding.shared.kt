package com.lightningkite.khrysalis.observables.binding

import android.view.View
import android.widget.LinearLayout
import com.lightningkite.khrysalis.escaping
import com.lightningkite.khrysalis.observables.*
import com.lightningkite.khrysalis.rx.removed
import com.lightningkite.khrysalis.rx.until
import com.lightningkite.khrysalis.views.geometry.AlignPair
import com.lightningkite.khrysalis.views.params

private class LinearLayoutBoundSubview<T>(val view: View, val property: StandardObservableProperty<T>)

fun <T> LinearLayout.bind(
    data: ObservableProperty<List<T>>,
    defaultValue: T,
    makeView: @escaping() (ObservableProperty<T>) -> View
) {
    val existingViews: ArrayList<LinearLayoutBoundSubview<T>> = ArrayList()
    data.subscribeBy { value ->
        //Fix view count
        val excessViews = existingViews.size - value.size
        if(excessViews > 0){
            //remove views
            for(iter in 1 .. excessViews) {
                val old = existingViews.removeAt(existingViews.lastIndex)
                this.removeView(old.view)
            }
        } else if(existingViews.size < value.size) {
            //add views
            for(iter in 1 .. (-excessViews)) {
                val prop = StandardObservableProperty(defaultValue)
                val view = makeView(prop)
                this.addView(view, this.params(gravity = AlignPair.centerFill))
                existingViews.add(LinearLayoutBoundSubview(view, prop))
            }
        }

        //Update views
        for(index in 0..value.size-1){
            existingViews[index].property.value = value[index]
        }
    }.until(this.removed)
}


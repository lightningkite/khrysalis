package com.lightningkite.khrysalis.observables.binding

import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.LinearLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.lightningkite.khrysalis.escaping
import com.lightningkite.khrysalis.observables.*
import com.lightningkite.khrysalis.views.EmptyView
import com.lightningkite.khrysalis.views.ViewDependency
import com.lightningkite.khrysalis.views.geometry.AlignPair
import com.lightningkite.khrysalis.views.params
import kotlin.reflect.KClass

private data class LinearLayoutBoundSubview<T>(val view: View, val property: StandardObservableProperty<T>)

fun <T> LinearLayout.bind(
    data: ObservableProperty<List<T>>,
    defaultValue: T,
    makeView: (ObservableProperty<T>) -> View
) {
    val existingViews = ArrayList<LinearLayoutBoundSubview<T>>()
    data.addAndRunWeak(this) { self, value ->
        //Fix view count
        val excessViews = existingViews.size - value.size
        if(excessViews > 0){
            //remove views
            for(iter in 1 .. excessViews) {
                val old = existingViews.removeAt(existingViews.lastIndex)
                self.removeView(old.view)
            }
        } else if(existingViews.size < value.size) {
            //add views
            for(iter in 1 .. (-excessViews)) {
                val prop = StandardObservableProperty(defaultValue)
                val view = makeView(prop)
                self.addView(view, self.params(gravity = AlignPair.centerFill))
                existingViews.add(LinearLayoutBoundSubview(view, prop))
            }
        }

        //Update views
        for(index in 0..value.size-1){
            existingViews[index].property.value = value[index]
        }
    }
}


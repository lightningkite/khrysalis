@file:Suppress("NAME_SHADOWING")

package com.lightningkite.khrysalis.observables.binding

import com.lightningkite.khrysalis.JsName
import com.lightningkite.khrysalis.observables.*
import com.lightningkite.khrysalis.rx.removed
import com.lightningkite.khrysalis.rx.until
import com.rd.PageIndicatorView


/**
 *
 * Binds the page Indicator to the observable provided and the count.
 * Count will describe how many screens exist, and the selected describes which one
 * will be visible.
 *
 *  Example
 *  val page = StandardObservableProperites(1)
 *  pageView.bind(numberOfPages, page)
 */

fun PageIndicatorView.bind(count: Int = 0, selected: MutableObservableProperty<Int>){
    this.count = count
    selected.subscribeBy{ value ->
        this.selection = value
    }.until(this.removed)
}

/**
 *
 * Binds the page Indicator to the observable provided and the count.
 * Count will describe how many screens exist, and the selected describes which one
 * will be visible.
 *
 *  Example
 *  val page = StandardObservableProperites(1)
 *  pageView.bind(numberOfPages, page)
 */
@JsName("comRdPageIndicatorViewBindDynamic")
fun PageIndicatorView.bind(count: ObservableProperty<Int> , selected: MutableObservableProperty<Int>){

    count.combine(selected){count, selected ->
        return@combine Pair(count,selected)
    }.subscribeBy { (count, selected) ->
        this.count = count
        this.selection = selected
    }.until(this.removed)
}

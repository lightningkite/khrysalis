package com.lightningkite.khrysalis.observables.binding

import android.view.View
import android.view.ViewGroup
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.lightningkite.khrysalis.observables.*
import com.lightningkite.khrysalis.rx.removed
import com.lightningkite.khrysalis.rx.until

/**
 *
 * Binds the items in the ViewPager to the list provided, and the showing index to the observable provided.
 * Any changes to the observable will change the current page. AS well updating the pager will update the observable.
 *
 */
fun <T> ViewPager.bind(
    items: List<T>,
    showIndex: MutableObservableProperty<Int> = StandardObservableProperty(0),
    makeView: (T)->View
) {
    adapter = object : PagerAdapter() {

        override fun isViewFromObject(p0: View, p1: Any): Boolean = p1 == p0

        override fun getCount(): Int = items.size

        override fun instantiateItem(container: ViewGroup, position: Int): Any {
            val data = items[position]
            val view = makeView(data)
            container.addView(view)
            return view
        }

        override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
            container.removeView(`object` as View)
        }
    }

    showIndex.subscribeBy{ value ->
        this.currentItem = value
    }.until(this.removed)
    this.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
        override fun onPageScrollStateChanged(p0: Int) {}
        override fun onPageScrolled(p0: Int, p1: Float, p2: Int) {}
        override fun onPageSelected(p0: Int) {
            showIndex.value = p0
        }
    })
}

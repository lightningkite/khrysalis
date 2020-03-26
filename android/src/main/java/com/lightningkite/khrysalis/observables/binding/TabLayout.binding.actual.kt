package com.lightningkite.khrysalis.observables.binding

import com.google.android.material.tabs.TabLayout
import com.lightningkite.khrysalis.observables.*
import com.lightningkite.khrysalis.rx.removed
import com.lightningkite.khrysalis.rx.until

fun TabLayout.bind(
    tabs: List<String>,
    selected: MutableObservableProperty<Int>
){
    for(tab in tabs){
        addTab(newTab().setText(tab))
    }
    selected.subscribeBy { value ->
        this.getTabAt(value)?.select()
    }.until(this.removed)
    this.addOnTabSelectedListener(object : TabLayout.BaseOnTabSelectedListener<TabLayout.Tab> {
        override fun onTabReselected(p0: TabLayout.Tab) {
            if(selected.value != p0.position)
                selected.value = p0.position
        }

        override fun onTabUnselected(p0: TabLayout.Tab) {}

        override fun onTabSelected(p0: TabLayout.Tab) {
            if(selected.value != p0.position)
                selected.value = p0.position
        }
    })
}

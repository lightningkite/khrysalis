package com.lightningkite.kwift.observables.binding

import com.google.android.material.tabs.TabLayout
import com.lightningkite.kwift.observables.*

fun TabLayout.bind(
    tabs: List<String>,
    selected: MutableObservableProperty<Int>
){
    for(tab in tabs){
        addTab(newTab().setText(tab))
    }
    selected.addAndRunWeak(this) { self, value ->
        self.getTabAt(value)?.select()
    }
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

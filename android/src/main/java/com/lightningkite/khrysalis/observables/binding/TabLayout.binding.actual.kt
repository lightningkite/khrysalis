package com.lightningkite.khrysalis.observables.binding

import com.google.android.material.tabs.TabLayout
import com.lightningkite.khrysalis.observables.*
import com.lightningkite.khrysalis.rx.removed
import com.lightningkite.khrysalis.rx.until

/**
 *
 * Binds the tabs as well as the selected tab to the data provided.
 * tabs is the title of each tab and it will create a tab for each of them.
 * selected is the tab number that will be selected. User selecting a tab
 * will update selected, as well modifying selected will manifest in the tabs.
 *
 */

fun TabLayout.bind(
    tabs: List<String>,
    selected: MutableObservableProperty<Int>,
    allowReselect:Boolean = false
) {
    for (tab in tabs) {
        addTab(newTab().setText(tab))
    }
    selected.subscribeBy { value ->
        this.getTabAt(value)?.select()
    }.until(this.removed)
    this.addOnTabSelectedListener(object : TabLayout.BaseOnTabSelectedListener<TabLayout.Tab> {

        var suppress = false

        override fun onTabReselected(p0: TabLayout.Tab) {
            if (!suppress && allowReselect) {
                suppress = true
                selected.value = p0.position
                suppress = false
            }
        }

        override fun onTabUnselected(p0: TabLayout.Tab) {}

        override fun onTabSelected(p0: TabLayout.Tab) {
            if (!suppress) {
                suppress = true
                if (selected.value != p0.position)
                    selected.value = p0.position
                suppress = false
            }
        }
    })
}


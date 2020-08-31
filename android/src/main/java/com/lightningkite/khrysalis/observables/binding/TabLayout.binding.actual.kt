package com.lightningkite.khrysalis.observables.binding

import com.google.android.material.tabs.TabLayout
import com.lightningkite.khrysalis.*
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

@JsName("tabLayoutBindIndex")
fun TabLayout.bind(
    tabs: List<String>,
    selected: MutableObservableProperty<Int>,
    allowReselect: Boolean = false
) {
    this.removeAllTabs()
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


fun <T: AnyEquatable> TabLayout.bind(
    tabs: List<T>,
    selected: MutableObservableProperty<T>,
    allowReselect:Boolean = false,
    toString: (T)->String = { it.toString() }
) {
    this.removeAllTabs()
    for (tab in tabs) {
        addTab(newTab().setText(tab.let(toString)))
    }
    selected.subscribeBy { value ->
        val index = tabs.indexOf(value)
        if(index != -1) {
            this.getTabAt(index)?.select()
        }
    }.until(this.removed)
    this.addOnTabSelectedListener(object : TabLayout.BaseOnTabSelectedListener<TabLayout.Tab> {

        var suppress = false

        override fun onTabReselected(p0: TabLayout.Tab) {
            if (!suppress && allowReselect) {
                suppress = true
                selected.value = tabs[p0.position]
                suppress = false
            }
        }

        override fun onTabUnselected(p0: TabLayout.Tab) {}

        override fun onTabSelected(p0: TabLayout.Tab) {
            if (!suppress) {
                suppress = true
                if (selected.value != p0.position)
                    selected.value = tabs[p0.position]
                suppress = false
            }
        }
    })
}



/**
 *
 * Binds the tabs as well as the selected tab to the data provided.
 * tabs is the title of each tab and it will create a tab for each of them.
 * selected is the tab number that will be selected. User selecting a tab
 * will update selected, as well modifying selected will manifest in the tabs.
 *
 */

@JsName("tabLayoutBindObservable")
fun <T : AnyHashable> TabLayout.bind(
    options: ObservableProperty<List<T>>,
    selected: MutableObservableProperty<T>,
    allowReselect: Boolean = false,
    toString: (T) -> String
) {
    val map = HashMap<T, TabLayout.Tab>()
    val reverse = HashMap<TabLayout.Tab, T>()
    options.subscribeBy { tabs ->
        val temp = selected.value
        var tabSelected = false
        this.removeAllTabs()
        map.clear()
        for (tab in tabs) {
            val actual = newTab().setText(toString(tab))
            map[tab] = actual
            reverse[actual] = tab
            if (temp == tab) tabSelected = true
            addTab(actual, temp == tab)
        }
        if (!tabSelected) map.entries.firstOrNull()?.value?.select()
    }.until(this.removed)

    selected.subscribeBy { value ->
        map[value]?.select()
    }.until(this.removed)
    this.addOnTabSelectedListener(object : TabLayout.BaseOnTabSelectedListener<TabLayout.Tab> {

        var suppress = false

        override fun onTabReselected(p0: TabLayout.Tab) {
            if (!suppress && allowReselect) {
                suppress = true
                val value = reverse[p0]
                if (value != null)
                    selected.value = value
                suppress = false
            }
        }

        override fun onTabUnselected(p0: TabLayout.Tab) {}

        override fun onTabSelected(p0: TabLayout.Tab) {
            if (!suppress) {
                suppress = true
                val value = reverse[p0]
                if (value != null && selected.value != value)
                    selected.value = value
                suppress = false
            }
        }
    })
}


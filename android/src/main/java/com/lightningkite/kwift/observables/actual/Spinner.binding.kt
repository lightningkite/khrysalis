package com.lightningkite.kwift.observables.actual

import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.BaseAdapter
import android.widget.Spinner
import com.lightningkite.kwift.observables.shared.MutableObservableProperty
import com.lightningkite.kwift.observables.shared.ObservableProperty
import com.lightningkite.kwift.observables.shared.StandardObservableProperty
import com.lightningkite.kwift.observables.shared.addAndRunWeak


fun <T> Spinner.bind(
    options: ObservableProperty<List<T>>,
    selected: MutableObservableProperty<T>,
    makeView: (ObservableProperty<T>) -> View
) {
    adapter = object : BaseAdapter() {
        init {
            options.addAndRunWeak(this) { self, _ ->
                self.notifyDataSetChanged()
            }
        }

        @Suppress("UNCHECKED_CAST")
        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            val view = convertView ?: run {
                val event = StandardObservableProperty<T>(
                    options.value.getOrNull(position) ?: selected.value
                )
                val subview = makeView(event)
                subview.tag = event
                return subview
            }
            (view.tag as? StandardObservableProperty<T>)?.let {
                it.value = options.value.getOrNull(position) ?: selected.value
            } ?: throw IllegalStateException()
            return view
        }

        override fun getItem(position: Int): Any? = options.value.getOrNull(position)
        override fun getItemId(position: Int): Long = position.toLong()
        override fun getCount(): Int = options.value.size
    }
    selected.addAndRunWeak(this) { self, it ->
        val index = options.value.indexOf(it)
        if (index != -1 && index != selectedItemPosition) {
            setSelection(index)
        }
    }
    onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
        override fun onNothingSelected(parent: AdapterView<*>?) {}

        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
            val newValue = options.value.getOrNull(position) ?: return
            if (selected.value != newValue) {
                selected.value = newValue
            }
        }
    }
}

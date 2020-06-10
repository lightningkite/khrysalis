package com.lightningkite.khrysalis.observables.binding

import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.BaseAdapter
import android.widget.Spinner
import android.widget.TextView
import com.lightningkite.khrysalis.JsName
import com.lightningkite.khrysalis.observables.*
import com.lightningkite.khrysalis.rx.removed
import com.lightningkite.khrysalis.rx.until


/**
 *
 * Binds the available options to the options Observable
 * Binds the selected item to the selected observable.
 * makeView is a lambda that return the view for each item in the drop down.
 *
 */

@JsName("spinnerBindAdvanced")
@JvmName("bindComplex")
fun <T> Spinner.bind(
    options: ObservableProperty<List<T>>,
    selected: MutableObservableProperty<T>,
    makeView: (ObservableProperty<T>) -> View
) {
    adapter = object : BaseAdapter() {
        init {
            options.subscribeBy { _ ->
                this.notifyDataSetChanged()
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
    selected.subscribeBy { it ->
        val index = options.value.indexOf(it)
        if (index != -1 && index != selectedItemPosition) {
            setSelection(index)
        }
    }.until(this.removed)
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

@JsName("spinnerBind")
fun <T> Spinner.bind(
    options: ObservableProperty<List<T>>,
    selected: MutableObservableProperty<T>,
    toString: (T) -> String
) {
    adapter = object : BaseAdapter() {
        init {
            options.subscribeBy { _ ->
                this.notifyDataSetChanged()
            }
        }

        @Suppress("UNCHECKED_CAST")
        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            val view = convertView ?: run {
                val event = StandardObservableProperty<T>(
                    options.value.getOrNull(position) ?: selected.value
                )
                val subview = TextView(context)
                val padding = (context.resources.displayMetrics.density * 8).toInt()
                subview.setPadding(padding,padding,padding,padding)
                subview.bindString(event.map(toString))
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
    selected.subscribeBy { it ->
        val index = options.value.indexOf(it)
        if (index != -1 && index != selectedItemPosition) {
            setSelection(index)
        }
    }.until(this.removed)
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

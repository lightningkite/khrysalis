package com.lightningkite.khrysalis.observables.binding

import com.lightningkite.khrysalis.observables.*
import android.os.Build
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.lightningkite.khrysalis.rx.removed
import com.lightningkite.khrysalis.rx.until


fun <T> AutoCompleteTextView.bind(
    options: ObservableProperty<List<T>>,
    toString: (T) -> String,
    onItemSelected: (T) -> Unit
) {
    val filtered = options.value.toMutableList()
    this.setAdapter(object : BaseAdapter(), Filterable {

        override fun getFilter(): Filter = object : Filter() {
            override fun performFiltering(p0: CharSequence?): FilterResults = FilterResults().apply {
                val query = p0?.toString()?.split(" ")?.takeUnless { it.isEmpty() }
                if (query != null) {
                    val newFilteredOptions =
                        options.value.filter { it -> query.all { q -> it.let(toString).contains(q, true) } }
                    this.values = newFilteredOptions
                    this.count = newFilteredOptions.size
                } else {
                    this.values = options.value
                    this.count = options.value.size
                }
            }

            @Suppress("UNCHECKED_CAST")
            override fun convertResultToString(resultValue: Any?): CharSequence {
                return (resultValue as? T)?.let(toString) ?: ""
            }

            @Suppress("UNCHECKED_CAST")
            override fun publishResults(p0: CharSequence?, p1: FilterResults?) {
                filtered.clear()
                filtered.addAll(p1?.values as? List<T> ?: listOf())
                notifyDataSetChanged()
            }
        }

        init {
            options.subscribeBy { text ->
                filter.filter(text.toString())
            }.until(this@bind.removed)
        }

        @Suppress("UNCHECKED_CAST")
        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            val view = (convertView as? TextView) ?: TextView(context).apply {
                setTextColor(this@bind.textColors)
                textSize = this@bind.textSize / resources.displayMetrics.scaledDensity
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    maxLines = this@bind.maxLines
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    letterSpacing = this@bind.letterSpacing
                }
                val size = (context.resources.displayMetrics.density * 8).toInt()
                setPadding(size, size, size, size)
            }
            view.text = filtered.getOrNull(position)?.let(toString)
            return view
        }

        override fun getItem(position: Int): Any? = filtered.getOrNull(position)
        override fun getItemId(position: Int): Long = position.toLong()
        override fun getCount(): Int = filtered.size
    })
    this.setOnItemClickListener { adapterView, view, index, id ->
        filtered.getOrNull(index)?.let(onItemSelected)
    }
}

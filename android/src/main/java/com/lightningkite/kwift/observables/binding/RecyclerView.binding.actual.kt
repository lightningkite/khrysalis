package com.lightningkite.kwift.observables.binding

import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.lightningkite.kwift.escaping
import com.lightningkite.kwift.observables.*
import com.lightningkite.kwift.views.EmptyView
import com.lightningkite.kwift.views.ViewDependency
import kotlin.reflect.KClass


fun RecyclerView.whenScrolledToEnd(action: () -> Unit) {
    addOnScrollListener(object : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            (layoutManager as? LinearLayoutManager)?.let {
                if (it.findLastVisibleItemPosition() == adapter?.itemCount?.minus(1)) {
                    action()
                }
            }
        }
    })
}

var RecyclerView.reverseDirection: Boolean
    get() = (this.layoutManager as? LinearLayoutManager)?.reverseLayout ?: false
    set(value){
        (this.layoutManager as? LinearLayoutManager)?.reverseLayout = value
    }

fun <T> RecyclerView.bind(
    data: ObservableProperty<List<T>>,
    defaultValue: T,
    makeView: (ObservableProperty<T>) -> View
) {
    layoutManager = LinearLayoutManager(context)
    adapter = object : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
        init {
            println("Setting up adapter")
            data.addAndRunWeak(this) { self, _ ->
                self.notifyDataSetChanged()
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            println("Creating view holder")
            val event = StandardObservableProperty<T>(defaultValue)
            val subview = makeView(event)
            subview.tag = event
            subview.layoutParams = RecyclerView.LayoutParams(MATCH_PARENT, WRAP_CONTENT)
            return object : RecyclerView.ViewHolder(subview) {}
        }

        override fun getItemCount(): Int = data.value.size

        @Suppress("UNCHECKED_CAST")
        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            (holder.itemView.tag as? StandardObservableProperty<T>)?.let {
                println("Updating value to ${data.value[position]}")
                it.value = data.value[position]
            } ?: run {
                println("Failed to find property to update")
            }
        }
    }
}

class RVTypeHandler(val viewDependency: ViewDependency) {
    class Handler(
        val type: KClass<*>,
        val defaultValue: Any,
        val handler: @escaping() (ObservableProperty<Any>)->View
    )
    internal var typeCount: Int = 0
        private set
    private val handlers: ArrayList<Handler> = ArrayList<Handler>()
    private val defaultHandler: Handler = Handler(
        type = Any::class,
        defaultValue = Unit,
        handler = { obs ->
            EmptyView(viewDependency)
        }
    )

    fun handle(type: KClass<*>, defaultValue: Any, action: @escaping() (ObservableProperty<Any>)->View ) {
        handlers += Handler(
            type = type,
            defaultValue = defaultValue,
            handler = action
        )
        typeCount++
    }
    inline fun <reified T: Any> handle(defaultValue: T, noinline action: @escaping() (ObservableProperty<T>)->View ) {
        handle(T::class, defaultValue) { obs ->
            action(obs.map { it as T })
        }
    }

    internal fun type(item: Any): Int {
        handlers.forEachIndexed { index, handler ->
            if(handler.type.isInstance(item)){
                return index
            }
        }
        return typeCount
    }
    internal fun make(type: Int): View {
        val handler = if(type < typeCount) handlers[type] else defaultHandler
        val event = StandardObservableProperty<Any>(handler.defaultValue)
        val subview = handler.handler(event)
        subview.tag = event
        subview.layoutParams = RecyclerView.LayoutParams(MATCH_PARENT, WRAP_CONTENT)
        return subview
    }
}

fun RecyclerView.bindMulti(
    viewDependency: ViewDependency,
    data: ObservableProperty<List<Any>>,
    typeHandlerSetup: (RVTypeHandler)->Unit
) {
    val typeHandler = RVTypeHandler(viewDependency).apply(typeHandlerSetup)
    layoutManager = LinearLayoutManager(context)
    adapter = object : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
        init {
            println("Setting up adapter")
            data.addAndRunWeak(this) { self, _ ->
                self.notifyDataSetChanged()
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            val subview = typeHandler.make(viewType)
            return object : RecyclerView.ViewHolder(subview) {}
        }

        override fun getItemViewType(position: Int): Int {
            return typeHandler.type(data.value.getOrNull(position) ?: return typeHandler.typeCount)
        }
        override fun getItemCount(): Int = data.value.size

        @Suppress("UNCHECKED_CAST")
        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            (holder.itemView.tag as? MutableObservableProperty<Any>)?.let {
                it.value = data.value[position]
            } ?: run {
                println("Failed to find property to update")
            }
        }
    }
}

fun <T> RecyclerView.bindMulti(
    data: ObservableProperty<List<T>>,
    defaultValue: T,
    determineType: (T)->Int,
    makeView: (Int, ObservableProperty<T>) -> View
) {
    layoutManager = LinearLayoutManager(context)
    adapter = object : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
        init {
            println("Setting up adapter")
            data.addAndRunWeak(this) { self, _ ->
                self.notifyDataSetChanged()
            }
        }

        override fun getItemViewType(position: Int): Int {
            return determineType(data.value[position])
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            println("Creating view holder")
            val event = StandardObservableProperty<T>(defaultValue)
            val subview = makeView(viewType, event)
            subview.tag = event
            subview.layoutParams = RecyclerView.LayoutParams(MATCH_PARENT, WRAP_CONTENT)
            return object : RecyclerView.ViewHolder(subview) {}
        }

        override fun getItemCount(): Int = data.value.size

        @Suppress("UNCHECKED_CAST")
        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            (holder.itemView.tag as? StandardObservableProperty<T>)?.let {
                println("Updating value to ${data.value[position]}")
                it.value = data.value[position]
            } ?: run {
                println("Failed to find property to update")
            }
        }
    }
}

fun RecyclerView.bindRefresh(
    loading: ObservableProperty<Boolean>,
    refresh: () -> Unit
) {
    (this.parent as? SwipeRefreshLayout)?.run {
        loading.addAndRunWeak(this) { self, value ->
            self.post {
                self.isRefreshing = value
            }
        }
        setOnRefreshListener {
            refresh()
        }
    }
}


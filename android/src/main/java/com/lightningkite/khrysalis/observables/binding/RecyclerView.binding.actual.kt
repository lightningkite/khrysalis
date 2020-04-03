package com.lightningkite.khrysalis.observables.binding

import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.lightningkite.khrysalis.escaping
import com.lightningkite.khrysalis.observables.*
import com.lightningkite.khrysalis.rx.removed
import com.lightningkite.khrysalis.rx.until
import com.lightningkite.khrysalis.views.newEmptyView
import com.lightningkite.khrysalis.views.ViewDependency
import kotlin.reflect.KClass

/**
 *
 *  Provides the RecyclerView a lambda to call when the lambda reaches the end of the list.
 *
 */

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

/**
 *
 *  When set to true will reverse the direction of the recycler view.
 *  Rather than top to bottom, it will scroll bottom to top.
 *
 */
var RecyclerView.reverseDirection: Boolean
    get() = (this.layoutManager as? LinearLayoutManager)?.reverseLayout ?: false
    set(value){
        (this.layoutManager as? LinearLayoutManager)?.reverseLayout = value
    }


/**
 *
 * Binds the data in the RecyclerView to the data provided by the Observable.
 * makeView is the lambda that creates the view tied to each item in the list of data.
 *
 * Example
 * val data = StandardObservableProperty(listOf(1,2,3,4,5))
 * recycler.bind(
 *  data = data,
 *  defaultValue = 0,
 *  makeView = { observable ->
 *       val xml = ViewXml()
 *       val view = xml.setup(dependency)
 *       view.text.bindString(obs.map{it -> it.toString()})
 *       return view
 *       }
 * )
 */

fun <T> RecyclerView.bind(
    data: ObservableProperty<List<T>>,
    defaultValue: T,
    makeView: (ObservableProperty<T>) -> View
) {
    layoutManager = LinearLayoutManager(context)
    adapter = object : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
        init {
            data.subscribeBy { _ ->
                this.notifyDataSetChanged()
            }.until(this@bind.removed)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
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
            newEmptyView(viewDependency)
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
            data.subscribeBy { _ ->
                this.notifyDataSetChanged()
            }.until(this@bindMulti.removed)
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


/**
 *
 * Binds the data in the RecyclerView to the data provided by the Observable.
 * This is designed for multiple types of views in the recycler view.
 * determineType is a lambda with an item input. The returned value is an Int determined by what view that item needs.
 * makeView is the lambda that creates the view for the type determined
 *
 * Example
 * val data = StandardObservableProperty(listOf(item1,item2,item3,item4,item5))
 * recycler.bind(
 *  data = data,
 *  defaultValue = Item(),
 *  determineType: { item ->
 *      when(item){
 *          ... return 1
 *          ... return 2
 *      }
 *  },
 *  makeView = { type, item ->
 *      when(type){
 *       1 -> {
 *          val xml = ViewXml()
 *          val view = xml.setup(dependency)
 *          view.text.bindString(item.map{it -> it.toString()})
 *          return view
 *            }
 *       2 -> {
 *          .... return view
 *            }
 *          }
 *      }
 * )
 */

fun <T> RecyclerView.bindMulti(
    data: ObservableProperty<List<T>>,
    defaultValue: T,
    determineType: (T)->Int,
    makeView: (Int, ObservableProperty<T>) -> View
) {
    layoutManager = LinearLayoutManager(context)
    adapter = object : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
        init {
            data.subscribeBy { _ ->
                this.notifyDataSetChanged()
            }.until(this@bindMulti.removed)
        }

        override fun getItemViewType(position: Int): Int {
            return determineType(data.value[position])
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
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
                it.value = data.value[position]
            } ?: run {
                println("Failed to find property to update")
            }
        }
    }
}


/**
 *
 *
 *
 */

fun RecyclerView.bindRefresh(
    loading: ObservableProperty<Boolean>,
    refresh: () -> Unit
) {
    (this.parent as? SwipeRefreshLayout)?.run {
        loading.subscribeBy { value ->
            this.post {
                this.isRefreshing = value
            }
        }.until(this@bindRefresh.removed)
        setOnRefreshListener {
            refresh()
        }
    }
}


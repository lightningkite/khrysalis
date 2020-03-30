package org.liftinggenerations.shared.views

import com.lightningkite.khrysalis.actuals.weak
import com.lightningkite.khrysalis.actuals.weakLambda
import com.lightningkite.khrysalis.shared.ObservableProperty
import com.lightningkite.khrysalis.shared.ViewData
import com.lightningkite.khrysalis.shared.ViewDataStack
import org.liftinggenerations.shared.api.API
import org.liftinggenerations.shared.models.*

class ResourceLibraryViewData(
    val session: Session,
    val stack: ViewDataStack,
    selectedProperty: ObservableProperty<List<Resource>>? = null
) : ViewData {
    var selected by weak(selectedProperty)

    override val title: String get() = "Resource Library"
    override val resourceName: String get() = "ResourceLibrary"

    val entries = ObservableProperty(underlyingValue = listOf<Resource>())
    val loading = ObservableProperty(underlyingValue = false)
    val areMorePages = ObservableProperty(underlyingValue = true)
    val page = ObservableProperty(underlyingValue = 1)

    val selectMode: Boolean
        get() {
            return selected != null
        }

    val category: ObservableProperty<Category> = ObservableProperty(underlyingValue = Category.anyCategory)
    val categoryOptions: ObservableProperty<List<Category>> =
        ObservableProperty(underlyingValue = listOf(Category.anyCategory))

    init {
        API.getCategories(token = session.token, onResult = weakLambda { code, options, error ->
            if (options != null) {
                val allOptions = arrayListOf<Category>()
                allOptions.add(Category.anyCategory)
                for (option in options) {
                    allOptions.add(option)
                }
                this.categoryOptions.value = allOptions
            }
        })
    }

    enum class Sort {
        None, Alphabetical, AlphabeticalReversed, Category
    }

    val sort: ObservableProperty<Sort> = ObservableProperty(underlyingValue = Sort.None)
    val sortOptions: ObservableProperty<List<Sort>> = ObservableProperty(
        underlyingValue = listOf<Sort>(
            Sort.None,
            Sort.Alphabetical,
            Sort.AlphabeticalReversed,
            Sort.Category
        )
    )

    val query = ObservableProperty("")
    val filteredEntries = ObservableProperty(underlyingValue = listOf<Resource>())

    init {
        filteredEntries.calculatedBy(entries, query, category, sort) { entries, query, category, sort ->
            val filtered = entries.filter { r ->
                if (query != "" && !r.title.toLowerCase().contains(query.toLowerCase())) {
                    return@filter false
                }
                if (category.id != Category.anyCategory.id && r.category != category.id) {
                    return@filter false
                }
                return@filter true
            }

            var sorted = filtered
            when (sort) {
                Sort.None -> {
                }
                Sort.Alphabetical -> {
                    sorted = sorted.sortedBy { it.title }
                }
                Sort.AlphabeticalReversed -> {
                    sorted = sorted.sortedByDescending { it.title }
                }
                Sort.Category -> {
                    sorted = sorted.sortedBy { it.category }
                }
            }

            return@calculatedBy sorted
        }
    }

    init {
        refresh()
    }

    fun loadMore() {
        if (!areMorePages.value) {
            return
        }
        if (loading.value) {
            return
        }
        loading.value = true

        API.getResources(
            token = session.token,
            page = page.value,
            onResult = weakLambda { code, result, error ->
                if (result != null) {
                    if (result.isEmpty()) {
                        this.areMorePages.value = false
                    } else {
                        this.entries.value = this.entries.value + result
                    }
                    this.page.value = this.page.value + 1
                } else {
                    //TODO: Error handle
                }
                this.loading.value = false
            }
        )
    }

    fun refresh() {
        loading.value = false
        areMorePages.value = true
        entries.value = listOf<Resource>()
        page.value = 1
        loadMore()
    }

    fun select(resource: Resource) {
        val selectedRef = selected
        if (selectedRef != null) {
            selectedRef.value = selectedRef.value + resource
        }
    }

    fun remove(resource: Resource) {
        val selectedRef = selected
        if (selectedRef != null) {
            selectedRef.value = selectedRef.value - resource
        }
    }
}

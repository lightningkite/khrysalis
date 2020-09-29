package com.lightningkite.khrysalis.ios.layout2.models

class SubstateAppendable(): Appendable {
    val normal = StringBuilder()
    var _selected: StringBuilder? = null
    val selected: StringBuilder get() {
        _selected?.let { return it }
        val newBuilder = StringBuilder(normal)
        _selected = newBuilder
        return newBuilder
    }
    var _highlighted: StringBuilder? = null
    val highlighted: StringBuilder get() {
        _highlighted?.let { return it }
        val newBuilder = StringBuilder(normal)
        _highlighted = newBuilder
        return newBuilder
    }
    var _disabled: StringBuilder? = null
    val disabled: StringBuilder get() {
        _disabled?.let { return it }
        val newBuilder = StringBuilder(normal)
        _disabled = newBuilder
        return newBuilder
    }
    var _focused: StringBuilder? = null
    val focused: StringBuilder get() {
        _focused?.let { return it }
        val newBuilder = StringBuilder(normal)
        _focused = newBuilder
        return newBuilder
    }

    var currentState: IosState? = null

    inline fun deferTo(action: Appendable.()->Unit) {
        when(currentState){
            IosState.Normal -> normal.action()
            IosState.Selected -> selected.action()
            IosState.Highlighted -> highlighted.action()
            IosState.Disabled -> disabled.action()
            IosState.Focused -> focused.action()
            null -> {
                normal.action()
                _selected?.action()
                _highlighted?.action()
                _disabled?.action()
                _focused?.action()
            }
        }
    }

    override fun append(string: CharSequence): Appendable {
        deferTo { append(string) }
        return this
    }

    override fun append(string: CharSequence, start: Int, end: Int): Appendable {
        deferTo { append(string, start, end) }
        return this
    }

    override fun append(c: Char): Appendable {
        deferTo { append(c) }
        return this
    }

    inline fun forState(state: IosState, action: ()->Unit) {
        val prev = this.currentState
        this.currentState = state
        action()
        this.currentState = prev
    }
    inline fun <T> forSubselector(selector: StateSelector<T>?, action: (T)->Unit) {
        if(selector == null) return
        forState(IosState.Normal) { action(selector.normal) }
        if(selector.selected != null) this.selected
        if(selector.highlighted != null) this.highlighted
        if(selector.disabled != null) this.disabled
        if(selector.focused != null) this.focused
        _selected?.let {
            forState(IosState.Selected) { action(selector.selected ?: selector.normal) }
        }
        _highlighted?.let {
            forState(IosState.Highlighted) { action(selector.highlighted ?: selector.normal) }
        }
        _disabled?.let {
            forState(IosState.Disabled) { action(selector.disabled ?: selector.normal) }
        }
        _focused?.let {
            forState(IosState.Focused) { action(selector.focused ?: selector.normal) }
        }
    }

    fun layer(name: String) = StateSelector(
        normal = IosDrawable(name, layer = normal.toString()),
        selected = _selected?.let { IosDrawable(name, layer = it.toString()) },
        highlighted = _highlighted?.let { IosDrawable(name, layer = it.toString()) },
        disabled = _disabled?.let { IosDrawable(name, layer = it.toString()) },
        focused = _focused?.let { IosDrawable(name, layer = it.toString()) }
    )
}
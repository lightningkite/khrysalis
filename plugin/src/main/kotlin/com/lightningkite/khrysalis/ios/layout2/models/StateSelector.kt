package com.lightningkite.khrysalis.ios.layout2.models

data class StateSelector<T>(
    val normal: T,
    val selected: T? = null,
    val highlighted: T? = null,
    val disabled: T? = null,
    val focused: T? = null
) {
    val isSet: Boolean get() = selected != null || highlighted != null || disabled != null || focused != null
    val variants: Map<String, T> get() = listOf(
        "" to normal,
        "_selected" to selected,
        "_highlighted" to highlighted,
        "_disabled" to disabled,
        "_focused" to focused
    ).filter { it.second != null }.associate { it.first to it.second!! }

    operator fun get(state: IosState): T = when(state){
        IosState.Normal -> normal
        IosState.Selected -> selected ?: normal
        IosState.Highlighted -> highlighted ?: normal
        IosState.Disabled -> disabled ?: normal
        IosState.Focused -> focused ?: normal
    }

    fun copy(state: IosState, to: T) = when(state){
        IosState.Normal -> copy(normal = to)
        IosState.Selected -> copy(selected = to)
        IosState.Highlighted -> copy(highlighted = to)
        IosState.Disabled -> copy(disabled = to)
        IosState.Focused -> copy(focused = to)
    }
}
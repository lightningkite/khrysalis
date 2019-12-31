package com.lightningkite.kwift.observables.shared

class ReferenceObservableProperty<T>(
    val get: ()->T,
    val set: (T)->Unit,
    override val onChange: Event<T>
) : MutableObservableProperty<T>() {
    override var value: T
        get() = this.get()
        set(value) {
            this.set(value)
        }
    override fun update() {
        //do nothing
    }
}

package com.lightningkite.khrysalis.rx

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.lightningkite.khrysalis.escaping
import com.lightningkite.khrysalis.observables.Closeable
import com.lightningkite.khrysalis.swiftDescendsFrom
import io.reactivex.disposables.Disposable

class DisposeCondition(val call: @escaping() (Disposable) -> Unit)

infix fun DisposeCondition.and(other: DisposeCondition): DisposeCondition =
    andAllDisposeConditions(listOf(this, other))

fun andAllDisposeConditions(list: List<DisposeCondition>): DisposeCondition = DisposeCondition { it ->
    var disposalsLeft = list.size
    for (item in list) {
        item.call(DisposableLambda {
            disposalsLeft -= 1
            if (disposalsLeft == 0) it.dispose()
        })
    }
}

infix fun DisposeCondition.or(other: DisposeCondition): DisposeCondition =
    DisposeCondition { it -> this.call(it); other.call(it) }

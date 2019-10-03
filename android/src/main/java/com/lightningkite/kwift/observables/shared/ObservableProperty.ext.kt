package com.lightningkite.kwift.observables.shared

import com.lightningkite.kwift.actuals.AnyObject
import com.lightningkite.kwift.actuals.escaping


fun <A : AnyObject, T> ObservableProperty<T>.addAndRunWeak(
    referenceA: A,
    listener: @escaping() (A, T) -> Unit
): Close = onChange.addAndRunWeak(
    referenceA = referenceA,
    value = value,
    listener = listener
)

fun <A : AnyObject, B : AnyObject, T> ObservableProperty<T>.addAndRunWeak(
    referenceA: A,
    referenceB: B,
    listener: @escaping() (A, B, T) -> Unit
): Close = onChange.addAndRunWeak(
    referenceA = referenceA,
    referenceB = referenceB,
    value = value,
    listener = listener
)

fun <A : AnyObject, B : AnyObject, C : AnyObject, T> ObservableProperty<T>.addAndRunWeak(
    referenceA: A,
    referenceB: B,
    referenceC: C,
    listener: @escaping() (A, B, C, T) -> Unit
): Close = onChange.addAndRunWeak(
    referenceA = referenceA,
    referenceB = referenceB,
    referenceC = referenceC,
    value = value,
    listener = listener
)

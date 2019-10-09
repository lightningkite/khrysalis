package com.lightningkite.kwift.observables.shared

import com.lightningkite.kwift.actual.escaping

class Close(val close: @escaping() () -> Unit)

package com.lightningkite.kwift.observables.shared

import com.lightningkite.kwift.actuals.escaping

class Close(val close: @escaping() () -> Unit)

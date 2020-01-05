package com.lightningkite.kwift.observables

import com.lightningkite.kwift.escaping

class Close(val close: @escaping() () -> Unit)

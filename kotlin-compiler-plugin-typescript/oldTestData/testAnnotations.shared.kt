package com.test.annot

import com.test.magicVariable
import kotlin.math.absoluteValue

@Throws(IllegalArgumentException::class) fun test(){}

val lambda: @Example(2) ()->Unit = {}
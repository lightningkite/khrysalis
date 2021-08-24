@file:SharedCode
package com.test.random

import kotlin.random.Random
import com.lightningkite.butterfly.*
import com.lightningkite.butterfly.views.geometry.*


fun main(){
    Random.nextFloat()
    Random.nextFloat(2f)
    Random.nextFloat(1f, 2f)
    Random.nextGFloat()
    Random.nextGFloat(2f)
    Random.nextGFloat(1f, 2f)
    Random.Default.nextFloat()
    Random.Default.nextFloat(2f)
    Random.Default.nextFloat(1f, 2f)
    Random.Default.nextGFloat()
    Random.Default.nextGFloat(2f)
    Random.Default.nextGFloat(1f, 2f)
}

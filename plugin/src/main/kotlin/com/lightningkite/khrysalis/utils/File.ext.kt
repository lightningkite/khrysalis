package com.lightningkite.khrysalis.utils

import java.io.File

fun File.writeTextIfDifferent(text: String){
    if(!this.exists() || this.readText() != text){
        this.writeText(text)
    }
}

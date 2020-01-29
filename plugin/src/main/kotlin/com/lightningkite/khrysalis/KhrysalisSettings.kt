package com.lightningkite.khrysalis

object KhrysalisSettings {
    var verbose = false
}

fun log(text: String){
    if(KhrysalisSettings.verbose){
        println(text)
    }
}

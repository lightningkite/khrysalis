package com.lightningkite.kwift

object KwiftSettings {
    var verbose = false
}

fun log(text: String){
    if(KwiftSettings.verbose){
        println(text)
    }
}

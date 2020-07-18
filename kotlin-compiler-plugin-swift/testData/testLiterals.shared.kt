package com.test

import android.view.View

private fun literalTest(){
    val item = "asdf"
    val item2 = "asdf$item"
    val item3 = "asdf${item}"
    val item4 = "asdf${1 + 2}"
    val item5 = """asdf
        multi
        line
        literal
    asdf"""
}
package com.test

fun Int.extensionPlusOne(): Int {
    return this + 1
}

fun <Element> List<Element>.estimateWork(): Int {
    return this.size
}

fun <Element, A> List<Element>.estimateWork(method: A, how: (Element, A)->Int): Int {
    var amount = 0
    for(item in this){
        amount += how(item, method)
    }
    return amount
}

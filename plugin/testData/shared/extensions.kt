package com.test

interface SomeInterface {}

fun Int.extensionPlusOne(): Int {
    return this + 1
}

fun <Element> List<Element>.estimateWork(): Int {
    return this.size
}

fun List<@swiftExactly("OVERRIDE") SomeInterface>.estimateWork2(): Int {
    return this.size
}

fun List<@swiftDescendsFrom("OVERRIDE") SomeInterface>.estimateWork3(): Int {
    return this.size
}

fun <S: @swiftExactly("OVERRIDE") SomeInterface> List<S>.estimateWork4(): Int {
    return this.size
}

fun <S: @swiftDescendsFrom("OVERRIDE") SomeInterface> List<S>.estimateWork5(): Int {
    return this.size
}

fun <Element, A> List<Element>.estimateWork(method: A, how: (Element, A)->Int): Int {
    var amount = 0
    for(item in this){
        amount += how(item, method)
    }
    return amount
}

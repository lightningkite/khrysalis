package com.test;

fun asdf(){
    a.onChange.add(listener = weakLambda { aValue ->
        val bValue = bWeak?.value
        if (bValue != null) {
            this.value = calculation(aValue, bValue)
        }
    })

}

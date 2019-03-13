package com.test

data class InitWithEscapingLambda(
    @escaping val listener: ()->Unit
)

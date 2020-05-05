package com.test.classes

import com.test.magicVariable
import kotlin.math.absoluteValue

interface TestInterface {
    val interfaceValue: String get() = "Default"
    fun interfaceFunction(): String = "Default"
}

data class DataClassThing(val x: Double = 0.0, val y: String = "Hello!"): TestInterface {
    override fun interfaceFunction(): String = "$x $y"
}

class Weird(a: Int = 0, b: String, val c: Double, var d: Long): TestInterface {
    val e: Int = 0
    var f: String
    init {
        f = "asdf"
    }

    override val interfaceValue: String
        get() = f
}

enum class Suits {
    SPADES, CLUBS, DIAMONDS, HEARTS;
}

enum class AdvancedSuits(val black: Boolean) {
    SPADES(true){
        override fun print(cardNum: Int){
            println("♠$cardNum")
        }
    },
    CLUBS(true){},
    DIAMONDS(false){},
    HEARTS(false){};

    open fun print(cardNum: Int) {
        println("$this$cardNum")
    }
}

fun main(){
    val outsideInfo: String = "Pulled in"
    val instance = object: TestInterface {
        override val interfaceValue: String
            get() = outsideInfo
    }
    val simpleSuit = Suits.CLUBS
    val advancedSuit = AdvancedSuits.DIAMONDS
    for(simp in Suits.values()){
        println(simp.name)
        println(Suits.valueOf(simp.name))
    }
    for(simp in AdvancedSuits.values()){
        println(simp.name)
        simp.print(3)
        println(AdvancedSuits.valueOf(simp.name))
    }
}
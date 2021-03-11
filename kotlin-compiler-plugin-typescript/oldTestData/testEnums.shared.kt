package com.test.classes

import com.test.annot.JsonProperty

enum class Suits {
    SPADES, CLUBS, DIAMONDS, HEARTS;
}

enum class AdvancedSuits(val black: Boolean) {
    SPADES(true){
        override fun printSelf(cardNum: Int){
            println("♠$cardNum")
        }
    },
    CLUBS(true){},
    DIAMONDS(false){},
    HEARTS(false){};

    open fun printSelf(cardNum: Int) {
        println("$this$cardNum")
    }
}

enum class StatusEnum(val comparableValue: Int, val darkColorResource: Int = 0, val colorResource: Int = 0, val textResource: Int = 0) : Codable {
    @JsonProperty("safe") Safe(comparableValue = 3, darkColorResource = 0, colorResource = 0, textResource = 0),
    @JsonProperty("unsafe") Unsafe(comparableValue = 1, colorResource = 0, textResource = 0),
    @JsonProperty("cleared") Cleared(comparableValue = 4, darkColorResource = 0, textResource = 0),
    @JsonProperty("unknown") Unknown(comparableValue = 2, darkColorResource = 0, colorResource = 0);
}

fun testEnums(){
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
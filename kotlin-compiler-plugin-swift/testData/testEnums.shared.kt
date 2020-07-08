package com.test

private enum class EnumSuits {
    SPADES, CLUBS, DIAMONDS, HEARTS;
}

private enum class EnumAdvancedSuits(val black: Boolean) {
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

private fun enumTest(){
    val simpleSuit = EnumSuits.CLUBS
    val advancedSuit = EnumAdvancedSuits.DIAMONDS
    for(simp in EnumSuits.values()){
        println(simp.name)
        println(EnumSuits.valueOf(simp.name))
    }
    for(simp in EnumAdvancedSuits.values()){
        println(simp.name)
        simp.print(3)
        println(EnumAdvancedSuits.valueOf(simp.name))
    }
}
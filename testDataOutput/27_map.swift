importimportimportjavajavajava...utilutilutil...***





funfunfunmainmainmain(((argsargsargs::: ArrayArrayArray<<<StringStringString>>>))){{{


// Constructors


valvalvalhashMap0hashMap0hashMap0===HashMapHashMapHashMap<<<StringStringString,,,StringStringString>>>((()))


valvalvallinkedHashMap0linkedHashMap0linkedHashMap0===LinkedHashMapLinkedHashMapLinkedHashMap<<<StringStringString,,,StringStringString>>>((()))


valvalvalmap0map0map0===emptyMapemptyMapemptyMap<<<StringStringString,,,StringStringString>>>((()))


valvalvalmap1map1map1===mapOfmapOfmapOf(((PairPairPair(((101010,,, """hihihi"""))))))


valvalvalmap2map2map2===mapOfmapOfmapOf(((PairPairPair((("""asasas""",,, """hihihi"""))),,, PairPairPair((("""dfdfdf""",,, """hellohellohello"""))),,, PairPairPair((("""ghghgh""",,, """salutsalutsalut"""))))))


varvarvarhashMaphashMaphashMap===hashMapOfhashMapOfhashMapOf(((PairPairPair(((111,,, """hihihi"""))))))


varvarvarlinkedMaplinkedMaplinkedMap===linkedMapOflinkedMapOflinkedMapOf(((PairPairPair(((111,,, """hihihi"""))))))


varvarvarmutableMapmutableMapmutableMap===mutableMapOfmutableMapOfmutableMapOf(((PairPairPair(((111,,, """hihihi"""))))))





// Basic calls


printlnprintlnprintln((("""${${${map1map1map1[[[101010]]]}}} (hi) (hi) (hi)""")))


printlnprintlnprintln((("""${${${map2map2map2[[["""asasas"""]]]}}} (hi) (hi) (hi)""")))


printlnprintlnprintln((("""${${${map2map2map2...sizesizesize}}} (3) (3) (3)""")))


hashMaphashMaphashMap...putputput(((222,,, """hellohellohello""")))


linkedMaplinkedMaplinkedMap...putAllputAllputAll(((linkedMaplinkedMaplinkedMap)))


mutableMapmutableMapmutableMap...removeremoveremove(((111)))





printlnprintlnprintln(((hashMaphashMaphashMap)))


ififif(((hashMaphashMaphashMap...sizesizesize !=!=!= 222))){{{


printlnprintlnprintln((("""ERROR: hashMap.sizeERROR: hashMap.sizeERROR: hashMap.size""")))


}}}


printlnprintlnprintln(((linkedMaplinkedMaplinkedMap)))


ififif(((linkedMaplinkedMaplinkedMap...sizesizesize !=!=!= 111))){{{


printlnprintlnprintln((("""ERROR: linkedMap.sizeERROR: linkedMap.sizeERROR: linkedMap.size""")))


}}}


printlnprintlnprintln(((mutableMapmutableMapmutableMap)))


ififif(((mutableMapmutableMapmutableMap...sizesizesize !=!=!= 000))){{{


printlnprintlnprintln((("""ERROR: mutableMap.sizeERROR: mutableMap.sizeERROR: mutableMap.size""")))


}}}





ififif(((!!!map0map0map0...isEmptyisEmptyisEmpty((()))))){{{


printlnprintlnprintln((("""ERROR: map0.isEmpty()ERROR: map0.isEmpty()ERROR: map0.isEmpty()""")))


}}}





printlnprintlnprintln((("""${${${hashMaphashMaphashMap...keyskeyskeys}}} ([2, 1]) ([2, 1]) ([2, 1])""")))


printlnprintlnprintln((("""${${${hashMaphashMaphashMap...valuesvaluesvalues}}} ([hello, hi]) ([hello, hi]) ([hello, hi])""")))





// Iteration


forforfor((((((kkk,,, vvv))) ininin map2map2map2))){{{


printlnprintlnprintln((("""$k$k$k :  :  : $v$v$v""")))


}}}


}}}


<EOF>
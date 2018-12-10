importimportimportjavajavajava...utilutilutil...***





funfunfunmainmainmain(((argsargsargs::: ArrayArrayArray<<<StringStringString>>>))){{{


// Constructors


valvalvalhashSet0hashSet0hashSet0===HashSetHashSetHashSet<<<StringStringString>>>((()))


valvalvallinkedHashSet0linkedHashSet0linkedHashSet0===LinkedHashSetLinkedHashSetLinkedHashSet<<<StringStringString>>>((()))


valvalvalset0set0set0===emptySetemptySetemptySet<<<StringStringString>>>((()))


valvalvalset1set1set1===setOfsetOfsetOf(((111)))


valvalvalset2set2set2===setOfsetOfsetOf(((111,,, 222,,, 111)))


varvarvarhashSethashSethashSet===hashSetOfhashSetOfhashSetOf(((111)))


varvarvarlinkedSetlinkedSetlinkedSet===linkedSetOflinkedSetOflinkedSetOf(((222)))


varvarvarmutableSetmutableSetmutableSet===mutableSetOfmutableSetOfmutableSetOf(((333)))





// Basic calls


printlnprintlnprintln((("""${${${set1set1set1}}} (1) (1) (1)""")))


printlnprintlnprintln((("""${${${set2set2set2}}} (1, 2) (1, 2) (1, 2)""")))


printlnprintlnprintln((("""${${${set2set2set2...sizesizesize}}} (2) (2) (2)""")))


hashSethashSethashSet...addaddadd(((222)))


linkedSetlinkedSetlinkedSet...addAlladdAlladdAll(((linkedSetlinkedSetlinkedSet)))


mutableSetmutableSetmutableSet...removeremoveremove(((333)))





printlnprintlnprintln(((hashSethashSethashSet)))


ififif(((hashSethashSethashSet...sizesizesize !=!=!= 222))){{{


printlnprintlnprintln((("""ERROR: hashSet.sizeERROR: hashSet.sizeERROR: hashSet.size""")))


}}}


printlnprintlnprintln(((linkedSetlinkedSetlinkedSet)))


ififif(((linkedSetlinkedSetlinkedSet...sizesizesize !=!=!= 111))){{{


printlnprintlnprintln((("""ERROR: linkedSet.sizeERROR: linkedSet.sizeERROR: linkedSet.size""")))


}}}


printlnprintlnprintln(((mutableSetmutableSetmutableSet)))


ififif(((mutableSetmutableSetmutableSet...sizesizesize !=!=!= 000))){{{


printlnprintlnprintln((("""ERROR: mutableSet.sizeERROR: mutableSet.sizeERROR: mutableSet.size""")))


}}}





ififif(((!!!set0set0set0...isEmptyisEmptyisEmpty((()))))){{{


printlnprintlnprintln((("""ERROR: set0.isEmpty()ERROR: set0.isEmpty()ERROR: set0.isEmpty()""")))


}}}





printlnprintlnprintln((("""${${${hashSethashSethashSet}}} ([2, 1]) ([2, 1]) ([2, 1])""")))





// Iteration


forforfor(((kkk ininin set2set2set2))){{{


printlnprintlnprintln((("""$k$k$k""")))


}}}


}}}


<EOF>
/* Output:
[1, 2, 3, 4, 10, 0]
[2, 4, 6, 8, 20, 0]
[2, 4, 6, 8, 20, 0]
10
20
["1€", "2€", "3€", "4€", "10€", "0€"]
["1€", "2€", "3€", "4€", "10€", "0€"]
1234100
*/





// SWIFT: extension Array {





// SWIFT: public func myMap<R>(transform: (Element) -> R) -> Array<R> {


funfunfun<<<TTT,,,RRR>>>ListListList<T>.myMap(((transformtransformtransform::: (((TTT))) ->->-> RRR))):::ListListList<<<RRR>>>{{{


valvalvalresultresultresult===arrayListOfarrayListOfarrayListOf<<<RRR>>>((()))


forforfor(((itemitemitem ininin thisthisthis))){{{


resultresultresult...addaddadd(((transformtransformtransform(((itemitemitem))))))


}}}


returnreturnreturnresultresultresult


// SWIFT: }





}}}





funfunfun<<<TTT>>>myMaxmyMaxmyMax(((collectioncollectioncollection::: CollectionCollectionCollection<<<TTT>>>,,, lesslessless::: (((TTT,,, TTT))) ->->-> BooleanBooleanBoolean))):::TTT???{{{


varvarvarmaxmaxmax:::TTT???===nullnullnull


forforfor(((ititit ininin collectioncollectioncollection))){{{


// SWIFT: if (max == nil || less(max!, it)) {


ififif(((maxmaxmax ====== nullnullnull |||||| lesslessless(((maxmaxmax,,, ititit)))))){{{


maxmaxmax===ititit


}}}


}}}


returnreturnreturnmaxmaxmax


}}}





funfunfunmainmainmain(((argsargsargs::: ArrayArrayArray<<<StringStringString>>>))){{{


valvalvalintsintsints===arrayListOfarrayListOfarrayListOf(((111,,, 222,,, 333,,, 444,,, 101010,,, 000)))


valvalvalints2ints2ints2===arrayListOfarrayListOfarrayListOf(((111,,, 222,,, 333,,, 444,,, 101010,,, 000)))


valvalvaldoubled1doubled1doubled1===intsintsints...myMapmyMapmyMap{{{elementelementelement->->->elementelementelement***222}}}


valvalvaldoubled2doubled2doubled2===intsintsints...myMapmyMapmyMap{{{ititit***222}}}





printlnprintlnprintln(((intsintsints)))


printlnprintlnprintln(((doubled1doubled1doubled1)))


printlnprintlnprintln(((doubled2doubled2doubled2)))





printlnprintlnprintln(((myMaxmyMaxmyMax(((ints2ints2ints2,,, lesslessless === {{{aaa,,, bbb ->->-> aaa <<< bbb}}}))))))


printlnprintlnprintln(((myMaxmyMaxmyMax(((doubled1doubled1doubled1,,, lesslessless === {{{aaa,,, bbb ->->-> aaa <<< bbb}}}))))))





// Use stdlib


printlnprintlnprintln(((intsintsints...mapmapmap {{{ """$it$it$it€€€"""}}} )))


printlnprintlnprintln(((intsintsints...mapmapmap((({{{"""$it$it$it€€€"""}}}))))))





// With parameter


intsintsints...forEachforEachforEach{{{intValintValintVal->->->


printprintprint(((intValintValintVal)))


}}}


}}}


<EOF>
funfunfunmainmainmain(((argsargsargs::: ArrayArrayArray<<<StringStringString>>>))){{{


trytrytry{{{


valvalvalxxx:::IntIntInt???===424242


valvalvalyyy:::IntIntInt???===nullnullnull





valvalvalx1x1x1===xxx?:?:?:000


valvalvaly1y1y1===yyy?:?:?:000


valvalvalx2x2x2===xxx?:?:?:throwthrowthrowExceptionExceptionException((("""FAIL: Should never happenFAIL: Should never happenFAIL: Should never happen""")))


valvalvaly2y2y2===yyy?:?:?:throwthrowthrowExceptionExceptionException((("""SUCCESS: Should happenSUCCESS: Should happenSUCCESS: Should happen""")))


printlnprintlnprintln((("""FailFailFail""")))


}}}catchcatchcatch(((eee::: ExceptionExceptionException))){{{


printlnprintlnprintln((("""SuccessSuccessSuccess""")))


}}}





valvalvalvaluesvaluesvalues:::ListListList<<<IntIntInt???>>>===arrayListOfarrayListOfarrayListOf(((111,,, 222,,, nullnullnull,,, nullnullnull,,, 333,,, 444,,, nullnullnull)))


forforfor(((valuevaluevalue ininin valuesvaluesvalues))){{{


valvalvalaaa===valuevaluevalue?:?:?:continuecontinuecontinue


printlnprintlnprintln(((aaa)))


}}}


}}}


<EOF>
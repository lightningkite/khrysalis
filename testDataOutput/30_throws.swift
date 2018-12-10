importimportimportjavajavajava...utilutilutil...***





@Throws@Throws@Throws(((javajavajava...langlanglang...IllegalStateExceptionIllegalStateExceptionIllegalStateException::::::classclassclass)))


funfunfunfoofoofoo((())){{{


throwthrowthrowjavajavajava...langlanglang...IllegalStateExceptionIllegalStateExceptionIllegalStateException((("""Do not call fooDo not call fooDo not call foo""")))


}}}





@Throws@Throws@Throws(((javajavajava...langlanglang...IllegalStateExceptionIllegalStateExceptionIllegalStateException::::::classclassclass)))funfunfunbarbarbar((())){{{


throwthrowthrowjavajavajava...langlanglang...IllegalStateExceptionIllegalStateExceptionIllegalStateException((("""Do not call barDo not call barDo not call bar""")))


}}}





@Throws@Throws@Throws(((javajavajava...langlanglang...IllegalStateExceptionIllegalStateExceptionIllegalStateException::::::classclassclass)))


funfunfunfooReturnsfooReturnsfooReturns((())):::StringStringString{{{


throwthrowthrowjavajavajava...langlanglang...IllegalStateExceptionIllegalStateExceptionIllegalStateException((("""Do not call fooReturnsDo not call fooReturnsDo not call fooReturns""")))


}}}





@Throws@Throws@Throws(((javajavajava...langlanglang...IllegalStateExceptionIllegalStateExceptionIllegalStateException::::::classclassclass)))funfunfunbarReturnsbarReturnsbarReturns((())):::IntIntInt{{{


throwthrowthrowjavajavajava...langlanglang...IllegalStateExceptionIllegalStateExceptionIllegalStateException((("""Do not call barDo not call barDo not call bar""")))


}}}





funfunfunmainmainmain(((argsargsargs::: ArrayArrayArray<<<StringStringString>>>))){{{


trytrytry{{{


foofoofoo((()))


barbarbar((()))


varvarvarxxx===fooReturnsfooReturnsfooReturns((()))


valvalvalyyy===barReturnsbarReturnsbarReturns((()))


}}}catchcatchcatch(((eee::: ExceptionExceptionException))){{{


printlnprintlnprintln((("""SuccessSuccessSuccess""")))


}}}


}}}


<EOF>
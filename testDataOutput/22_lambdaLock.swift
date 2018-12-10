classclassclassLockLockLock((())){{{


funfunfunlocklocklock((())){{{


printlnprintlnprintln((("""  locked  locked  locked""")))


}}}


funfunfununlockunlockunlock((())){{{


printlnprintlnprintln((("""  unlocked  unlocked  unlocked""")))


}}}


}}}





funfunfun<<<TTT>>>locklocklock(((locklocklock::: LockLockLock,,, bodybodybody::: ((())) ->->-> TTT))):::TTT{{{


locklocklock...locklocklock((()))


valvalvalxxx===bodybodybody((()))


locklocklock...unlockunlockunlock((()))


returnreturnreturnxxx


}}}





funfunfunmainmainmain(((argsargsargs::: ArrayArrayArray<<<StringStringString>>>))){{{


valvalvallockObjlockObjlockObj===LockLockLock((()))





// Simple lock


printlnprintlnprintln((("""before lockbefore lockbefore lock""")))


locklocklock(((lockObjlockObjlockObj))){{{


printlnprintlnprintln((("""    currently locked    currently locked    currently locked""")))


}}}


printlnprintlnprintln((("""after lockafter lockafter lock""")))


}}}


<EOF>
openopenopenclassclassclassBaseBaseBase{{{


openopenopenfunfunfunfff((())){{{}}}


funfunfunggg((())){{{


printlnprintlnprintln((("""g() calledg() calledg() called""")))


}}}


}}}





abstractabstractabstractclassclassclassDerivedDerivedDerived:::BaseBaseBase((())){{{


overrideoverrideoverrideabstractabstractabstractfunfunfunfff((()))


}}}





classclassclassDerived2Derived2Derived2:::BaseBaseBase((())){{{


overrideoverrideoverridefunfunfunfff((())){{{


printlnprintlnprintln((("""f() calledf() calledf() called""")))


}}}


}}}





funfunfunmainmainmain(((argsargsargs::: ArrayArrayArray<<<StringStringString>>>))){{{


Derived2().f()


Derived2().g()


}


<EOF>
funfunfunfoofoofoo(((aaa::: StringStringString,,, bbb::: MapMapMap<<<StringStringString,,, StringStringString>>>,,, ccc::: ((((((StringStringString))) ->->-> UnitUnitUnit)))??? === nullnullnull))):::StringStringString???{{{


ccc???...invokeinvokeinvoke((("""a=a=a=$a$a$a""")))


returnreturnreturnaaa


}}}





funfunfunfooFuncfooFuncfooFunc(((aaa::: StringStringString,,, bbb::: MapMapMap<<<StringStringString,,, StringStringString>>>,,, ccc::: ((((((StringStringString))) ->->-> UnitUnitUnit)))??? === nullnullnull))):::StringStringString???{{{


returnreturnreturnnullnullnull


}}}





funfunfunrunrunrun((())):::StringStringString{{{


varvarvarouterValueouterValueouterValue===""""""


varvarvaraaa===foofoofoo((("""barbarbar""",,, bbb === HashMapHashMapHashMap<<<StringStringString,,, StringStringString>>>((())),,, ccc === {{{ valuevaluevalue ->->-> outerValueouterValueouterValue === valuevaluevalue }}})))?:?:?:returnreturnreturn"""fail1fail1fail1"""


aaa===foofoofoo((("""barbarbar""",,, bbb === HashMapHashMapHashMap<<<StringStringString,,, StringStringString>>>((())),,, ccc === {{{ valuevaluevalue ->->-> outerValueouterValueouterValue === valuevaluevalue }}})))


fooFuncfooFuncfooFunc((("""barbarbar""",,, bbb === HashMapHashMapHashMap<<<StringStringString,,, StringStringString>>>((())),,, ccc === {{{ valuevaluevalue ->->-> outerValueouterValueouterValue === valuevaluevalue }}})))





ififif(((aaa !=!=!= """barbarbar"""))){{{


printlnprintlnprintln((("""fail: a=fail: a=fail: a=$a$a$a (should be bar) (should be bar) (should be bar)""")))


returnreturnreturn"""fail2fail2fail2"""


}}}





ififif(((outerValueouterValueouterValue !=!=!= """a=bara=bara=bar"""))){{{


printlnprintlnprintln((("""fail: outerValue=fail: outerValue=fail: outerValue=$outerValue$outerValue$outerValue (should be a=bar) (should be a=bar) (should be a=bar)""")))


returnreturnreturn"""fail3fail3fail3"""


}}}





returnreturnreturn"""okokok"""


}}}





funfunfunmainmainmain(((argsargsargs::: ArrayArrayArray<<<StringStringString>>>))){{{


ififif(((runrunrun((())) !=!=!= """okokok"""))){{{


printlnprintlnprintln((("""fail: invalid returnfail: invalid returnfail: invalid return""")))


}}}elseelseelse{{{


printlnprintlnprintln((("""successsuccesssuccess""")))


}}}


}}}


<EOF>
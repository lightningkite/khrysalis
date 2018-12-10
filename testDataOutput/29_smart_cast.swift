importimportimportjavajavajava...utilutilutil...***





funfunfunmainmainmain(((argsargsargs::: ArrayArrayArray<<<StringStringString>>>))){{{


valvalvalxxx:::IntIntInt???===111


varvarvaryyy:::IntIntInt===222


valvalvalzzz:::IntIntInt???===nullnullnull





ififif(((xxx !=!=!= nullnullnull))){{{


yyy+=+=+=xxx


}}}elseelseelse{{{


printlnprintlnprintln((("""FAIL: x should not be nullFAIL: x should not be nullFAIL: x should not be null""")))


}}}





ififif(((zzz !=!=!= nullnullnull))){{{


printlnprintlnprintln((("""FAIL: z should be nullFAIL: z should be nullFAIL: z should be null""")))


yyy+=+=+=zzz


}}}





ififif(((yyy !=!=!= 333))){{{


printlnprintlnprintln((("""FAIL: y should be 3, is FAIL: y should be 3, is FAIL: y should be 3, is $y$y$y""")))


}}}


printlnprintlnprintln((("""SuccessSuccessSuccess""")))


}}}


<EOF>
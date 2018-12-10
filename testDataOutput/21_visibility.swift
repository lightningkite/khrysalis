/* Output:
Nested init
234
Nested init
com.moshbit.kotlift.Outer$Nested@4c5e43ee (or similar)
Nested init
5
78910348910
*/





// Uncommented code will not compile due to visibility modifiers





openopenopeninternalinternalinternalclassclassclassOuterOuterOuter{{{


privateprivateprivatevalvalvalaaa===111


protectedprotectedprotectedvalvalvalbbb===222


internalinternalinternalvalvalvalccc===333


valvalvalddd===444// public by default


protectedprotectedprotectedvalvalvalnnn===NestedNestedNested((()))





protectedprotectedprotectedclassclassclassNestedNestedNested{{{


internalinternalinternalvalvalvaleee:::IntIntInt===555


initinitinit{{{


printlnprintlnprintln((("""Nested initNested initNested init""")))


}}}


}}}





privateprivateprivatefunfunfunooo((())):::IntIntInt{{{


returnreturnreturn666


}}}


protectedprotectedprotectedfunfunfunppp((())):::IntIntInt{{{


returnreturnreturn777


}}}


internalinternalinternalfunfunfunqqq((())):::IntIntInt{{{


returnreturnreturn888


}}}


funfunfunrrr((())):::IntIntInt{{{


returnreturnreturn999


}}}


publicpublicpublicfunfunfunsss((())):::IntIntInt{{{


returnreturnreturn101010


}}}


}}}





protectedprotectedprotectedclassclassclassSubclassSubclassSubclass:::OuterOuterOuter((())){{{


// a is not visible


// b, c and d are visible


// Nested and e are visible





funfunfunprintAllprintAllprintAll((())){{{


// println(a)


printprintprint(((bbb)))


printprintprint(((ccc)))


printlnprintlnprintln(((ddd)))





printlnprintlnprintln(((OuterOuterOuter...NestedNestedNested((())))))


println(Nested().e)





// print(o())


print(p())


print(q())


print(r())


print(s())


}}}


}





classclassclassUnrelatedUnrelatedUnrelated(((valvalval ooo::: OuterOuterOuter))){{{


// o.a, o.b are not visible


// o.c and o.d are visible (same module)


// Outer.Nested and Nested::e are not visible. In Swift they are visible, as there is no Protected.





funfunfunprintAllprintAllprintAll((())){{{


// println(o.a)


// println(o.b) // This statement runs in Swift, as there is no Protected.


printprintprint(((ooo...ccc)))


printprintprint(((ooo...ddd)))





/* // It is OK that the following 3 lines run in Swift:
    val nested = Outer.Nested()
    println(nested)
    println(nested.e)*/





// print(o.o())


// print(o.p()) // This statement runs in Swift, as there is no Protected.


printprintprint(((ooo...qqq((())))))


printprintprint(((ooo...rrr((())))))


printprintprint(((ooo...sss((())))))


}}}


}}}





funfunfunmainmainmain(((argsargsargs::: ArrayArrayArray<<<StringStringString>>>))){{{


valvalvalxxx===SubclassSubclassSubclass((()))


xxx...printAllprintAllprintAll((()))





valvalvalyyy===UnrelatedUnrelatedUnrelated(((ooo::: xxx)))


yyy...printAllprintAllprintAll((()))


}}}


<EOF>
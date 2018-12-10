// See https://kotlinlang.org/docs/reference/null-safety.html





classclassclassDepartmentDepartmentDepartment{{{


varvarvarheadheadhead:::UserUserUser???===nullnullnull


}}}





classclassclassUserUserUser(((varvarvar namenamename::: StringStringString??? === nullnullnull))){{{


varvarvardepartmentdepartmentdepartment:::DepartmentDepartmentDepartment???===nullnullnull


}}}





funfunfunmainmainmain(((argsargsargs::: ArrayArrayArray<<<StringStringString>>>))){{{


varvarvaraaa:::StringStringString==="""abcabcabc"""


//a = null // compilation error





varvarvarbbb:::StringStringString???==="""abcabcabc"""


bbb===nullnullnull// ok





printlnprintlnprintln((("""a.length = a.length = a.length = ${${${aaa...lengthlengthlength}}} (should be 3) (should be 3) (should be 3)""")))





//val blInvalid = b.length // error: variable 'b' can be null





// SWIFT: let bl = b != nil ? b!.length : -1


valvalvalblblbl===ififif(((bbb !=!=!= nullnullnull)))bbb...lengthlengthlengthelseelseelse---111


printlnprintlnprintln((("""bl = bl = bl = $bl$bl$bl (should be -1) (should be -1) (should be -1)""")))





// SWIFT: if b != nil && b!.length > 0 {


ififif(((bbb !=!=!= nullnullnull &&&&&& bbb...lengthlengthlength >>> 000))){{{


// SWIFT: print("ERROR: String of length \(b!.length)")


printlnprintlnprintln((("""ERROR: String of length ERROR: String of length ERROR: String of length ${${${bbb...lengthlengthlength}}}""")))


}}}elseelseelse{{{


printlnprintlnprintln((("""OK: Empty stringOK: Empty stringOK: Empty string""")))


}}}





printlnprintlnprintln((("""${${${bbb???...lengthlengthlength}}} should be null should be null should be null""")))





// Classes


valvalvalbobbobbob===UserUserUser(((namenamename === """BobBobBob""")))


valvalvaljohnjohnjohn===UserUserUser(((namenamename === """JohnJohnJohn""")))


valvalvalmarketingmarketingmarketing===DepartmentDepartmentDepartment((()))


bob.department=marketing


marketing.head=john


println("${bob.department?.head?.name} should be John")





b="new b value"


println("${b!!.length} should be 11")





valvalvalaIntaIntaInt:::IntIntInt???===aas?Int


println("$aInt should be null")


}


<EOF>
classclassclassFunkyClassFunkyClassFunkyClass{{{


varvarvarinternalStringinternalStringinternalString===""""""





varvarvarwrappedPropertywrappedPropertywrappedProperty:::StringStringString


getgetget((())){{{returnreturnreturn"""My string is My string is My string is $internalString$internalString$internalString"""}}}


setsetset(((valuevaluevalue))){{{


internalStringinternalStringinternalString==="""$value$value$value - previous= - previous= - previous=\"\"\"$internalString$internalString$internalString\"\"\""""


}}}


}}}





valvalvalcomputedProperty1computedProperty1computedProperty1:::IntIntInt


getgetget((())){{{


varvarvaraaa===000


aaa++++++


returnreturnreturnaaa


}}}





valvalvalcomputedProperty2computedProperty2computedProperty2:::IntIntInt


getgetget((())){{{returnreturnreturn222}}}





/*val computedProperty3: Int
  get() = 3*/








varvarvar_backingProperty_backingProperty_backingProperty:::IntIntInt===000


varvarvarcomputedProperty4computedProperty4computedProperty4:::IntIntInt


getgetget((())){{{


returnreturnreturn444+++_backingProperty_backingProperty_backingProperty


}}}


setsetset(((valuevaluevalue))){{{


_backingProperty_backingProperty_backingProperty===valuevaluevalue


}}}





varvarvarcomputedProperty5computedProperty5computedProperty5:::IntIntInt


getgetget((())){{{returnreturnreturn555+++_backingProperty_backingProperty_backingProperty}}}


setsetset(((valuevaluevalue))){{{_backingProperty_backingProperty_backingProperty===valuevaluevalue}}}





/*var computedProperty6: Int
  get() = 6 + _backingProperty
  set(value) { _backingProperty = value }*/





funfunfunmainmainmain(((argsargsargs::: ArrayArrayArray<<<StringStringString>>>))){{{


valvalvalxxx===FunkyClassFunkyClassFunkyClass((()))





println(x.wrappedProperty)


x.wrappedProperty="abc"


println(x.wrappedProperty)


x.wrappedProperty="123"


printlnprintlnprintln(((xxx...wrappedPropertywrappedPropertywrappedProperty)))





printlnprintlnprintln(((computedProperty1computedProperty1computedProperty1)))


printlnprintlnprintln(((computedProperty2computedProperty2computedProperty2)))


//println(computedProperty3)





printlnprintlnprintln(((computedProperty4computedProperty4computedProperty4)))


printlnprintlnprintln(((computedProperty5computedProperty5computedProperty5)))


//println(computedProperty6)


computedProperty4computedProperty4computedProperty4===100010001000


printlnprintlnprintln(((computedProperty4computedProperty4computedProperty4)))


printlnprintlnprintln(((computedProperty5computedProperty5computedProperty5)))


//println(computedProperty6)


}}}


<EOF>
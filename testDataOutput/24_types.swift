funfunfunmainmainmain(((argsargsargs::: ArrayArrayArray<<<StringStringString>>>))){{{


// Defined types


valvalvaldoubledoubledouble:::DoubleDoubleDouble===64.6464.6464.64


valvalvalfloatfloatfloat:::FloatFloatFloat===32.32f32.32f32.32f


valvalvallonglonglong:::LongLongLong===646464


valvalvalintintint:::IntIntInt===323232


valvalvalshortshortshort:::ShortShortShort===161616


valvalvalbytebytebyte:::ByteByteByte===888





// Auto types


valvalvaldouble2double2double2===64.6464.6464.64


valvalvalfloat2float2float2===32.32f32.32f32.32f


valvalvallong2long2long2===64L64L64L


valvalvalint2int2int2===323232








ififif(((doubledoubledouble !=!=!= double2double2double2))){{{


printlnprintlnprintln((("""double errordouble errordouble error""")))


}}}


ififif(((floatfloatfloat !=!=!= float2float2float2))){{{


printlnprintlnprintln((("""float errorfloat errorfloat error""")))


}}}


ififif(((longlonglong !=!=!= long2long2long2))){{{


printlnprintlnprintln((("""long errorlong errorlong error""")))


}}}


ififif(((intintint !=!=!= int2int2int2))){{{


printlnprintlnprintln((("""int errorint errorint error""")))


}}}





valvalvalsumsumsum===doubledoubledouble+++floatfloatfloat+++longlonglong+++intintint+++shortshortshort+++bytebytebyte+++double2double2double2+++float2float2float2+++long2long2long2+++int2int2int2





if(Math.abs(sum - 409.9199993896484) > 0.01){


printlnprintlnprintln((("""sum errorsum errorsum error""")))


}}}





if(Math.max(100L, long) != 100L){


println("max error")


}





if(Math.min(32f, float) != 32f){


println("min error")


}





print("finished")


}


<EOF>
//Package: com.test
//Converted using Khrysalis2


classSillyBox(valx:number)


classStuff(box:SillyBox){
    valweakBox:SillyBox | nullbyweak(box)
}


export function main(): void {
    valsillyBox=SillyBox(3)
    valstuff=Stuff(sillyBox)
    println(stuff.weakBox?.x?.toString()??"-")
}


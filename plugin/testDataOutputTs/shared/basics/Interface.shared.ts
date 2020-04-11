//Package: com.test
//Converted using Khrysalis2


interfaceMyInterface{
    valx:stringget()=""
    y(aString: string): string {
        returnx+aString
    }
    
}


classImplOverX:MyInterface{
    overridevalx:string
    get()="Hello!"
}


classImplOverY:MyInterface{
    y(aString: string): string {
        return"${x}!"
    }
    
}


classImplBoth:MyInterface{
    overridevalx:string
    get()="Hello!"
    y(aString: string): string {
        return"${x}!"
    }
    
}


export function main(): void {
    valitems:Array<MyInterface>=listOf(ImplBoth(),ImplOverX(),ImplOverY())
    for (const item of items) {
        println(item.x);
        println(item.y("Input"))
    }
}


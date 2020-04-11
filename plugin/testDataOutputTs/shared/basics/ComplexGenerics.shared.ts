//Package: com.test
//Converted using Khrysalis2


dataclassNameTag<T:Hashable>(valitem:T,valname:string)


export function printInterface<T extends MyInterface>(): void {
    println(`Hello!  I am ${name}, I stand for ${item.x}.`)
}



interfaceMyInterface{
    valx:stringget()=""
    y(aString: string): string {
        returnx+aString
    }
    
}


dataclassImplOverX(valk:number=0):MyInterface{
    overridevalx:string
    get()="Hello!"
}


dataclassImplOverY(valk:number=0):MyInterface{
    y(aString: string): string {
        return"${x}!"
    }
    
}


dataclassImplBoth(valk:number=0):MyInterface{
    overridevalx:string
    get()="Hello!"
    y(aString: string): string {
        return"${x}!"
    }
    
}


export function main(): void {
    NameTag(ImplBoth(),"ImplBoth").printInterface()
    NameTag(ImplOverX(),"ImplOverX").printInterface()
    NameTag(ImplOverY(),"ImplOverY").printInterface()
}



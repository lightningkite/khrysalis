//Package: com.test
//Converted using Khrysalis2


objectSingleton{
    valx:number=0
    doThing(argA: string, argB: number = 3): void {
        println("Hello World!")
    }
    
}


export function main(): void {
    Singleton.doThing("asdf",3)
}


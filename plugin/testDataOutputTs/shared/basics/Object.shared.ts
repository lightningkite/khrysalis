//Package: com.test
//Converted using Khrysalis2


object Singleton { 
     public x : number;
     doThing(argA: string, argB: number = (3)): void {
        console.log("Hello World!")
    }
     
 }
 

export function main(): void {
    Singleton.doThing("asdf", (3))
}


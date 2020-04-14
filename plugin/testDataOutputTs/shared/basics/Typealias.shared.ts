//Package: com.test
//Converted using Khrysalis2


export type MyInteger = number
export let MyInteger = number
 
 

class Thing {
    
    public constructor() {
    }
    
}
 
 

export type MyThing = Thing
export let MyThing = Thing
 

export type MyList< T > = Array<T>
export let MyList = Array
 

export type ListOfThings = MyList<MyThing>
export let ListOfThings = MyList
 
 

export function main(): void {
    const stuff : ListOfThings = listOf(new MyThing(), new Thing());
    console.log("Success")
}
 

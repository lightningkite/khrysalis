//Package: com.test
//Converted using Khrysalis2


export type MyInteger = number;
export let MyInteger = Number;



class Thing{}


export type MyThing = Thing;
export let MyThing = Thing;


export type MyList<T> = Array<T>;
export let MyList = Array;


export type ListOfThings = MyList<MyThing>;
export let ListOfThings = MyList;



export function main(): void {
    let stuff:ListOfThings = [new MyThing(), new Thing()];
    println("Success")
}


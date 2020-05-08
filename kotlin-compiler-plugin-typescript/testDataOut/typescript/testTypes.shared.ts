// Generated by Khrysalis TypeScript converter
// File: C:/Users/josep/Projects/khrysalis/kotlin-compiler-plugin-typescript/testData/testTypes.shared.kt
// Package: com.test.types
// Imported FQ name: com.test.classes.Weird TS Weird
// Imported FQ name: com.test.types.ListOfThings SKIPPED due to same file
// Imported FQ name: com.test.types.ListOfThings TS ListOfThings
// Imported FQ name: com.test.types.MyInteger SKIPPED due to same file
// Imported FQ name: com.test.types.MyInteger TS MyInteger
// Imported FQ name: com.test.types.MyList SKIPPED due to same file
// Imported FQ name: com.test.types.MyList TS MyList
// Imported FQ name: com.test.types.MyList.T TS T
// Imported FQ name: com.test.types.MyThing SKIPPED due to same file
// Imported FQ name: com.test.types.MyThing TS MyThing
// Imported FQ name: com.test.types.Thing SKIPPED due to same file
// Imported FQ name: com.test.types.Thing TS Thing
// Imported FQ name: com.test.types.main.ugh TS ugh
// Imported FQ name: com.test.types.main.unknownThing TS unknownThing
import { checkIsInterface, tryCastClass, tryCastInterface, tryCastPrimitive } from 'khrysalis/dist/Kotlin'
import { Weird } from 'testClasses.shared'

export type MyInteger = number;
export let MyInteger = Number;


export class Thing {
    public constructor() {
    }
}

export type MyThing = Thing;
export let MyThing = Thing;

export type MyList<T> = Array<T>;
export let MyList = Array;

export type ListOfThings = MyList<MyThing>;
export let ListOfThings = MyList;


export function main(){
    const x: number = 0;
    
    const y: MyInteger = 0;
    
    const stuff: ListOfThings = [new MyThing(), new Thing()];
    
    const otherList: Array<number> = [1, 2, 3];
    
    const nullabilityTest: (number | null) = null;
    
    const nullabilityTest2: (ListOfThings | null) = null;
    
    console.log("Success");
    
    const ugh = Weird.constructorkotlinInt(2);
    
    const unknownThing: (any | null) = ugh;
    
    
    if(checkIsInterface(unknownThing, "ComTestClassesTestInterface")){
        console.log("Hello!");
    }
    console.log(tryCastInterface(unknownThing, "ComTestClassesTestInterface"));
    
    if(unknownThing instanceof Weird){
        console.log("Hello!");
    }
    console.log(tryCastClass(unknownThing, Weird));
    
    if(typeof (unknownThing) == "number"){
        console.log("Hello!");
    }
    console.log(tryCastPrimitive(unknownThing, "number"));
}


// Generated by Khrysalis TypeScript converter
// File: /home/josephivie/IdeaProjects/khrysalis/kotlin-compiler-plugin-typescript/testData/testIfAndWhen.shared.kt
// Package: com.test.ifandwhen
// Imported FQ name: com.test.ifandwhen.main.result TS result
// Imported FQ name: com.test.ifandwhen.main.thing TS thing
// Imported FQ name: com.test.ifandwhen.main.thing2 TS thing2
// Imported FQ name: com.test.ifandwhen.main.thing3 TS thing3
// Imported FQ name: com.test.ifandwhen.makeSomething SKIPPED due to same file
// Imported FQ name: com.test.ifandwhen.makeSomething TS makeSomething
// Imported FQ name: com.test.ifandwhen.setNullable SKIPPED due to same file
// Imported FQ name: com.test.ifandwhen.setNullable TS setNullable
// Imported FQ name: kotlin.Any TS Any
// Imported FQ name: kotlin.Int TS Int
// Imported FQ name: kotlin.String TS String
// Imported FQ name: kotlin.io.println TS println

export function setNullable(): (Int | null){ return 2; }
export function makeSomething(): (Any | null){ return "Hello"; }

export function main(){
    let thing: Int = 0;
    
    
    //If/else chaining
    
    if(thing.equals(1)){
        println("is 1");
    }
    
    if(thing.equals(0)){
        println("is zero");
    } else {
        println("is not zero");
    }
    
    if(thing.equals(0)){
        println("is zero");
    } else if(thing.equals(1)){
        println("is one");
    } else {
        println("is more");
    }
    
    //If nullable smart cast
    let thing2: (Int | null) = setNullable();
    
    if(!(thing2.equals(null))){
        println("Thing is not null");
        const result = 3.plus(thing2);
        
        println(result);
    }
    
    if(thing2.equals(null)){
        println("thing is null");
    } else {
        println("Thing is not null");
        const result = 3.plus(thing2);
        
        println(result);
    }
    
    if(!(thing2.equals(null)) && thing.equals(0)){
        println(thing2.plus(thing));
    }
    
    //When on subject
    switch(thing){
        case 0:
        println("is zero")
        break;
        case 1:
        println("is one")
        break;
        case 2:
        println("is two")
        println("which is magical")
        break;
        default:
        println("is something else")
        break;
    }
    ;
    
    //When on conditions
    if(thing.equals(1)){
        println("thing is one")
    }else if(!(thing2.equals(null))){
        println("thing2 is not null")
    }else {
        println("else")
    };
    
    //when on subject advanced
    switch(thing2){
        case 0:
        println("is zero")
        break;
        case null:
        println("is null")
        break;
        default:
        println("is something")
        break;
    }
    ;
    
    //when on subject typed
    let thing3: (Any | null) = makeSomething();
    
    if(typeof (thing3) == "String"){
        println("Found string ".plus(thing3))
    }else if(typeof (thing3) == "Int"){
        println(`Found int ${thing3}`)
    }else if(thing3 == null){
        println("Found null")
    }else {
        println("Found something else")
    };
}

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

export function setNullable(): (number | null){ return 2; }
export function makeSomething(): (any | null){ return "Hello"; }

export function main(){
    let thing: number = 0;
    
    
    //If/else chaining
    
    if(thing === 1){
        console.log("is 1");
    }
    
    if(thing === 0){
        console.log("is zero");
    } else {
        console.log("is not zero");
    }
    
    if(thing === 0){
        console.log("is zero");
    } else if(thing === 1){
        console.log("is one");
    } else {
        console.log("is more");
    }
    
    //If nullable smart cast
    let thing2: (number | null) = setNullable();
    
    if(!(thing2 === null)){
        console.log("Thing is not null");
        const result = 3 + thing2;
        
        console.log(result);
    }
    
    if(thing2 === null){
        console.log("thing is null");
    } else {
        console.log("Thing is not null");
        const result = 3 + thing2;
        
        console.log(result);
    }
    
    if(!(thing2 === null) && thing === 0){
        console.log(thing2 + thing);
    }
    
    //When on subject
    switch(thing){
        case 0:
        console.log("is zero")
        break;
        case 1:
        console.log("is one")
        break;
        case 2:
        console.log("is two")
        console.log("which is magical")
        break;
        default:
        console.log("is something else")
        break;
    }
    ;
    
    //When on conditions
    if(thing === 1){
        console.log("thing is one")
    }else if(!(thing2 === null)){
        console.log("thing2 is not null")
    }else {
        console.log("else")
    };
    
    //when on subject advanced
    switch(thing2){
        case 0:
        console.log("is zero")
        break;
        case null:
        console.log("is null")
        break;
        default:
        console.log("is something")
        break;
    }
    ;
    
    //when on subject typed
    let thing3: (any | null) = makeSomething();
    
    if(typeof (thing3) == "string"){
        console.log("Found string " + thing3)
    }else if(typeof (thing3) == "number"){
        console.log(`Found int ${thing3}`)
    }else if(thing3 == null){
        console.log("Found null")
    }else {
        console.log("Found something else")
    };
}

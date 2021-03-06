// Generated by Khrysalis TypeScript converter - this file will be overwritten.
// File: testIfAndWhen.shared.kt
// Package: com.test.ifandwhen
import { safeEq } from 'butterfly-web/dist/Kotlin'
import { runOrNull } from 'butterfly-web/dist/kotlin/Language'

//! Declares com.test.ifandwhen.setNullable
export function setNullable(): (Int | null) { 
    return 2; 
}
//! Declares com.test.ifandwhen.makeSomething
export function makeSomething(): (Any | null) { 
    return "Hello"; 
}

//! Declares com.test.ifandwhen.main
export function main(): void {
    let thing: Int = 0;
    
    
    //If/else chaining
    
    if (safeEq(thing, 1)) {
        println("is 1");
    }
    
    if (safeEq(thing, 0)) {
        println("is zero");
    } else {
        println("is not zero");
    }
    
    if (safeEq(thing, 0)) {
        println("is zero");
    } else if (safeEq(thing, 1)) {
        println("is one");
    } else {
        println("is more");
    }
    
    //If nullable smart cast
    let thing2: (Int | null) = setNullable();
    
    if (thing2 !== null) {
        println("Thing is not null");
        const result = 3.plus(thing2!);
        
        println(result);
    }
    
    if (thing2 === null) {
        if (makeSomething() !== null) {
            makeSomething();
        } else {
            makeSomething();
        }
    } else {
        if (makeSomething() !== null) {
            makeSomething();
            thing = thing.plus(1);
        } else {
            makeSomething();
            thing = thing.plus(1);
        }
    }
    
    if (thing2 === null) {
        println("thing is null");
    } else {
        println("Thing is not null");
        const result = 3.plus(thing2!);
        
        println(result);
    }
    
    if (thing2 !== null && safeEq(thing, 0)) {
        println(thing2!.plus(thing));
    }
    
    const ifAsExpression = thing2 !== null ? (thing2 as ) : 0;
    
    const ifAsExpression2 = ((): Int => {
            if (thing2 !== null) {
                println("Hi!");
                return (thing2 as );
            } else {
                println("SAD");
                return 0;
            }
    })();
    
    
    const ifAsExpression3 = xIterableMap<Int, Int>(listOf<Int>(1, 2, 3), (it: Int): Int => {
            if (it.rem(2) === 0) { return it } else { return it.plus(1) }
    });
    
    
    const subfunction = (): Int => {
        return thing2 !== null ? (thing2 as ) : 0;
    }
    
    const subfunction2 = (): void => {
        if (thing2 !== null) {
            println("yeah");
            println("it's right here");
            thing = thing.inc();
        } else {
            println("No");
            thing = thing.inc();
        }
    }
    
    const subfunction3 = (): Int => {
        return ((): Int => {
                if (thing2 !== null) {
                    println("Hi3!");
                    return (thing2 as );
                } else {
                    println("SAD3");
                    return 0;
                }
        })();
    }
    
    if (safeEq(thing, 0)) {
        const it_17 = thing2;
        if (it_17 !== null) { 
            println(`It's a ${it_17}`);
        }
        println("Did the thing");
    }
    
    //Safe let
    const it_18 = thing2;
    if (it_18 !== null) { 
        println(`It's a ${it_18}`);
        const it_19 = thing2;
        if (it_19 !== null) { 
            println(`ANOTHER: ${it_19}`);
        }
    } else {
        const it_20 = thing2;
        if (it_20 !== null) { 
            println(`It's a ${it_20}`);
        } else {
            println("Dunno what it is");
        }
    }
    
    //Safe let single
    const it_21 = thing2;
    if (it_21 !== null) { 
        if (safeEq(subfunction3(), 0)) {
            println("Hiii");
        }
    }
    
    //When on subject
    switch(thing) {
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
        throw IllegalArgumentException.constructorString("NO!")
    }
    
    
    const calcInverted = (): Int => { 
        return ((): Int => {
                switch(thing) {
                    case 0:
                    return 2
                    case 1:
                    return 1
                    case 2:
                    return 0
                    default:
                    throw IllegalArgumentException.constructorString("NO!")
                }
                
        })(); 
    }
    
    //When on conditions
    if (safeEq(thing, 1)) {
        println("thing is one")
    } else if (thing2 !== null) {
        println("thing2 is not null")
    } else  {
        throw IllegalArgumentException.constructorString("NO!")
    }
    
    const calcWeird = (): Int => { 
        return ((): Int => {
                if (safeEq(thing, 1)) {
                    return 0
                } else if (thing2 !== null) {
                    return 3
                } else  {
                    throw IllegalArgumentException.constructorString("NO!")
                }
        })(); 
    }
    
    //when on subject advanced
    switch(thing2) {
        case 0:
        println("is zero")
        break;
        case null:
        println("is null")
        break;
        default:
        throw IllegalArgumentException.constructorString("NO!")
    }
    
    
    //when on subject typed
    let thing3: (Any | null) = makeSomething();
    
    if (typeof (thing3) == "String") {
        println("Found string ".plus(thing3))
    } else if (typeof (thing3) == "Int") {
        println(`Found int ${thing3}`)
    } else if (thing3 == null) {
        println("Found null")
    } else  {
        throw IllegalArgumentException.constructorString("NO!")
    }
}

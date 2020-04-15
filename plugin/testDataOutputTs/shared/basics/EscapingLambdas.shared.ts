//Package: com.test
//Converted using Khrysalis2


class HasLambda {
    action:  @ escaping () () => void;
    
    public constructor(action:  @ escaping () () => void = { 
     }) {
        this.action = action;
    }
    
    invoke(): void {
        action()
    }
}
 
 

let globalThing : () => void = { 
 } 

export function doThing(action:  @ escaping () () => void): void {
    globalThing = action
}
 
 

export function main(): void {
    (new HasLambda{ 
             console.log("Hello world!")
     }).invoke();
    doThing{ 
         console.log("Hello world 2!")
     };
    globalThing()
}
 

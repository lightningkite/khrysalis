//Package: com.test
//Converted using Khrysalis2


class HasLambda {
    action:  () => void;
    
    public constructor(action:  () => void = () => {
    }) {
        this.action = action;
    }
    
    invoke(): void {
        this.action()
    }
}

 

let globalThing : () => void = () => {
} 
;
export function doThing(action:  () => void): void {
    globalThing = action
}

 

export function main(): void {
    (new HasLambda(() => {console.log("Hello world!")
    })).invoke();
    doThing(() => {console.log("Hello world 2!")
    });
    globalThing()
}


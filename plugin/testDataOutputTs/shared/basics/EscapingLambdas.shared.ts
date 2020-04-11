//Package: com.test
//Converted using Khrysalis2


classHasLambda(valaction: @escaping()() => void={}){
    invoke(): void {
        action()
    }
    
}


varglobalThing:() => void={}

export function doThing(action:  @escaping()() => void): void {
    globalThing=action
}



export function main(): void {
    HasLambda{
        println("Hello world!")
    }.invoke()
    doThing{
        println("Hello world 2!")
    }
    globalThing()
}


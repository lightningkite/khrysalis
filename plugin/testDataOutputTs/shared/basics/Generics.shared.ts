//Package: com.test
//Converted using Khrysalis2


dataclassNameTag<T:Hashable>(valitem:T,valname:string)


export function printSelf<T extends Hashable>(): void {
    println("Hello!  I am ${name}.")
}



export function printInt(): void {
    println("Hello!  I am ${name}, I stand for ${item}.")
}



export function printAlt<T extends Hashable>(nameTag: NameTag<T>): void {
    println(`Hello!  I am ${nameTag.name}`)
}



export function main(): void {
    valtag=NameTag(2.toInt(),"Two")
    tag.printSelf()
    tag.printInt()
    printAlt(tag)
}


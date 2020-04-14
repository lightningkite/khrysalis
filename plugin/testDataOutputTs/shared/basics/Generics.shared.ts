//Package: com.test
//Converted using Khrysalis2


class NameTag< T : Hashable > {
    item: T;
    name: string;
    
    public constructor(item: T, name: string) {
        this.item = item;
        this.name = name;
    }
    
}
 
 

export function printSelfT extends Hashable(): void {
    console.log("Hello!  I am ${name}.")
}
 
 

export function printInt(): void {
    console.log("Hello!  I am ${name}, I stand for ${item}.")
}
 
 

export function printAltT extends Hashable(nameTag: NameTag<T>): void {
    console.log(`Hello!  I am ${nameTag.name}`)
}
 
 

export function main(): void {
    const tag = new NameTag(2.toInt(), "Two");
    tag.printSelf();
    tag.printInt();
    printAlt(tag)
}
 

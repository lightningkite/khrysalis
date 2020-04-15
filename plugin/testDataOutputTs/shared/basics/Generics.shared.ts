//Package: com.test
//Converted using Khrysalis2


class NameTag<T extends Hashable> {
    item: T;
    name: string;
    
    public constructor(item: T, name: string) {
        this.item = item;
        this.name = name;
    }
    
}

 

export function printSelf<T extends Hashable>(): void {
    console.log(`Hello!  I am ${this.name}.`)
}

 

export function printInt(): void {
    console.log(`Hello!  I am ${this.name}, I stand for ${this.item}.`)
}

 

export function printAlt<T extends Hashable>(nameTag: NameTag<T>): void {
    console.log(`Hello!  I am ${nameTag.name}`)
}

 

export function main(): void {
    const tag = new NameTag((2).toInt(), "Two");
    tag.printSelf();
    tag.printInt();
    printAlt(tag)
}


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

 

export function printInterface<T extends MyInterface>(): void {
    console.log(`Hello!  I am ${this.name}, I stand for ${this.item.x}.`)
}

 

interface MyInterface {
    implementsInterfaceMyInterface: boolean;
    readonly x : string;
    y(aString: string): string ;
}

 

class ImplOverX {
    k: number;
    
    public constructor(k: number = (0)) {
        this.k = k;
    }
    
    public x : string;
}

 

class ImplOverY {
    k: number;
    
    public constructor(k: number = (0)) {
        this.k = k;
    }
    
    y(aString: string): string {
        return `${this.x}!`
    }
}

 

class ImplBoth {
    k: number;
    
    public constructor(k: number = (0)) {
        this.k = k;
    }
    
    public x : string;
    y(aString: string): string {
        return `${this.x}!`
    }
}

 

export function main(): void {
    (new NameTag(new ImplBoth(), "ImplBoth")).printInterface();
    (new NameTag(new ImplOverX(), "ImplOverX")).printInterface();
    (new NameTag(new ImplOverY(), "ImplOverY")).printInterface()
}

 

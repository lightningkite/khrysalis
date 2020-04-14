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
 
 

export function printInterfaceT extends MyInterface(): void {
    console.log(`Hello!  I am ${name}, I stand for ${item.x}.`)
}
 
 

class MyInterface {
    
    public constructor() {
    }
    
    public x : string;
    y(aString: string): string {
        return x + aString
    }
}
 
 

class ImplOverX implements MyInterface {
    k: number;
    
    public constructor(k: number = 0) {
        this.k = k;
    }
    
    public x : string;
}
 
 

class ImplOverY implements MyInterface {
    k: number;
    
    public constructor(k: number = 0) {
        this.k = k;
    }
    
    y(aString: string): string {
        return "${x}!"
    }
}
 
 

class ImplBoth implements MyInterface {
    k: number;
    
    public constructor(k: number = 0) {
        this.k = k;
    }
    
    public x : string;
    y(aString: string): string {
        return "${x}!"
    }
}
 
 

export function main(): void {
    (new NameTag(new ImplBoth(), "ImplBoth")).printInterface();
    (new NameTag(new ImplOverX(), "ImplOverX")).printInterface();
    (new NameTag(new ImplOverY(), "ImplOverY")).printInterface()
}
 
 

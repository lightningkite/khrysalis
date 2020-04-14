//Package: com.test
//Converted using Khrysalis2


class MyInterface {
    
    public constructor() {
    }
    
    public x : string;
    y(aString: string): string {
        return x + aString
    }
}
 
 

class ImplOverX implements MyInterface {
    
    public constructor() {
    }
    
    public x : string;
}
 
 

class ImplOverY implements MyInterface {
    
    public constructor() {
    }
    
    y(aString: string): string {
        return "${x}!"
    }
}
 
 

class ImplBoth implements MyInterface {
    
    public constructor() {
    }
    
    public x : string;
    y(aString: string): string {
        return "${x}!"
    }
}
 
 

export function main(): void {
    const items : Array<MyInterface> = listOf(new ImplBoth(), new ImplOverX(), new ImplOverY());
    for (const item of items) {
        console.log(item.x);
        console.log(item.y("Input"))
    }
}
 

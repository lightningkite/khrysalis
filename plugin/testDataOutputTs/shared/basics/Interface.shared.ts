//Package: com.test
//Converted using Khrysalis2


interface MyInterface {
    implementsInterfaceMyInterface: boolean;
    readonly x : string;
    y(aString: string): string ;
}

 

class ImplOverX {
    
    public constructor() {
    }
    
    public x : string;
}

 

class ImplOverY {
    
    public constructor() {
    }
    
    y(aString: string): string {
        return `${this.x}!`
    }
}

 

class ImplBoth {
    
    public constructor() {
    }
    
    public x : string;
    y(aString: string): string {
        return `${this.x}!`
    }
}

 

export function main(): void {
    const items : Array<MyInterface> = listOf(new ImplBoth(), new ImplOverX(), new ImplOverY());
    for (const item of items) {
        console.log(item.x);
        console.log(item.y("Input"))
    }
}


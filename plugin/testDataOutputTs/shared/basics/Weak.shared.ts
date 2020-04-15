//Package: com.test
//Converted using Khrysalis2


class SillyBox {
    x: number;
    
    public constructor(x: number) {
        this.x = x;
    }
    
}
 
 

class Stuff {
    
    public constructor(box: SillyBox) {
    }
    
    public weakBox : SillyBox | null;
}
 
 

export function main(): void {
    const sillyBox = new SillyBox(3);
    const stuff = new Stuff(sillyBox);
    console.log(stuff.weakBox?.x?.toString() ?? "-")
}
 

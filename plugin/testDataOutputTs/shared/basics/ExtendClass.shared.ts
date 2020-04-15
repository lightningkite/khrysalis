//Package: com.test
//Converted using Khrysalis2


class Record {
    
    public constructor() {
        console.log("Record created: ${x}, ${y}")
    }
    
    public x : number;
    public y : string;
    test(): void {
        console.log("Test run")
    }
}
 
 

class BetterRecord extends Record {
    
    public constructor() {
        super();
    }
    
    public z : number;
    test(): void {
        console.log(`Test run ${z.toInt()}`)
    }
}
 
 

export function main(): void {
    const record = new BetterRecord();
    record .x = 3;
    record .y = "Hello";
    record .z = 32.1;
    (() => {if (record.x == 3){
                record .y = "Set"
    }})();
    record.test();
    console.log(`x: ${record.x}, y: ${record.y}`)
}
 

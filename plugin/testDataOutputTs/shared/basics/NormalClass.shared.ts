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
 
 

export function main(): void {
    const record = new Record();
    record .x = 3;
    record .y = "Hello";
    if (record.x == 3){
        record .y = "Set"
    };
    record.test();
    console.log(`x: ${record.x}, y: ${record.y}`)
}
 

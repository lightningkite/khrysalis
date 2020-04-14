//Package: com.test
//Converted using Khrysalis2


class Record {
    x: number;
    y: string;
    
    public constructor(x: number, y: string) {
        this.x = x;
        this.y = y;
        console.log("Record created: ${x}, ${y}")
    }
    
    test(): void {
        console.log("Test run")
    }
}
 
 

export function main(): void {
    const record = new Record(3, "Hello");
    if (record.x == 3){
        record .y = "Set"
    };
    console.log(`x: ${record.x}, y: ${record.y}`);
    const copy = record.copy(32);
    console.log(`x: ${copy.x}, y: ${copy.y}`);
    if (copy != record){
        console.log("Not equal")
    };
    record.test()
}
 

//Package: com.test
//Converted using Khrysalis2


class Record {
    x: number;
    y: string;
    
    public constructor(x: number, y: string) {
        this.x = x;
        this.y = y;
        console.log(`Record created: ${this.x}, ${this.y}`)
    }
    
    test(): void {
        const test : number = (0);
        console.log("Test run")
    }
    public static theMeaning : Record = new Record((42), "The Question");
    public static make(x: number, y: string): Record {
        return new Record(x, y);
    }
}

 

export function main(): void {
    Record.theMeaning.test();
    Record.make((43), "One more").test()
}


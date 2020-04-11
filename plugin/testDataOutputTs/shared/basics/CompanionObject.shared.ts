//Package: com.test
//Converted using Khrysalis2


dataclassRecord(varx:number,vary:string){
    init{
        println("Record created: ${x}, ${y}")
    }
    test(): void {
        println("Test run")
    }
    
    
    companionobject{
        valtheMeaning=Record(42,"The Question")
        make(x: number, y: string): Record {
            return Record(x,y);
        }
        
    }
}


export function main(): void {
    Record.theMeaning.test()
    Record.make(43,"One more").test()
}


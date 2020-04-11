//Package: com.test
//Converted using Khrysalis2


dataclassRecord(varx:number,vary:string){
    init{
        println("Record created: ${x}, ${y}")
    }
    test(): void {
        println("Test run")
    }
    
}


export function main(): void {
    valrecord=Record(x=3,y="Hello")
    if (record.x==3){
        record.y="Set"
    }
    println(`x: ${record.x}, y: ${record.y}`)
    valcopy=record.copy(x=32)
    println(`x: ${copy.x}, y: ${copy.y}`)
    if (copy!=record){
        println("Not equal")
    }
    record.test()
}


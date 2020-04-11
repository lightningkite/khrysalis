//Package: com.test
//Converted using Khrysalis2


classRecord{
    varx:number=0
    vary:string=""
    init{
        println("Record created: ${x}, ${y}")
    }
    test(): void {
        println("Test run")
    }
    
}


export function main(): void {
    valrecord=Record()
    record.x=3
    record.y="Hello"
    if (record.x==3){
        record.y="Set"
    }
    record.test()
    println(`x: ${record.x}, y: ${record.y}`)
}


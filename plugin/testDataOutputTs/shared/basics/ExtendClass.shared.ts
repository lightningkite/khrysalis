//Package: com.test
//Converted using Khrysalis2


openclassRecord{
    varx:number=0
    vary:string=""
    init{
        println("Record created: ${x}, ${y}")
    }
    test(): void {
        println("Test run")
    }
    
}


classBetterRecord:Record(){
    varz:number=0.0
    test(): void {
        println(`Test run ${z.toInt()}`)
    }
    
}


export function main(): void {
    valrecord=BetterRecord()
    record.x=3
    record.y="Hello"
    record.z=32.1
    if (record.x==3){
        record.y="Set"
    }
    record.test()
    println(`x: ${record.x}, y: ${record.y}`)
}


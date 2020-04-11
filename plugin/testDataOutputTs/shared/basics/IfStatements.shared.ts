//Package: com.test
//Converted using Khrysalis2


export function main(): void {
    varaNumber:number=0
    if (true){
        aNumber+=1
    } else {
        aNumber-=1
    }
    
    if (false){
        aNumber+=2
    } else {
        aNumber-=2
    }
    
    varthing:string | null=null
    thing?.let{
        aNumber+=4
    }??run{
        aNumber-=4
    }
    
    thing="Hello"
    thing?.let{
        aNumber+=8
    }??run{
        aNumber-=8
    }
    
    thing?.substringBefore(",")?.let{
        aNumber+=16
    }??run{
        aNumber-=16
    }
    
    if (thing!=null){
        println(thing);
        aNumber+=32
    }
    
    if (thingisstring){
        println(thing);
        aNumber+=64
    }
    
    println(aNumber)
}


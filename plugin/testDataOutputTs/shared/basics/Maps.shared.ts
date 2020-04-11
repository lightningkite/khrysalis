//Package: com.test
//Converted using Khrysalis2


export function main(): void {
    valmap:Record<number, string>=mapOf(1to"A",2to"B",3to"C")
    assert(map[1]=="A")
    assert(map[2]=="B")
    assert(map[3]=="C")
    assert(map[0]==null)
    
    valmutableMap:Record<number, string>=mutableMapOf(1to"A",2to"B",3to"C")
    assert(mutableMap[1]=="A")
    assert(mutableMap[2]=="B")
    assert(mutableMap[3]=="C")
    assert(mutableMap[0]==null)
    mutableMap[0]="-"
    assert(mutableMap[0]=="-")
    mutableMap.remove(0)
    assert(mutableMap[0]==null)
    mutableMap.put(0,"x")
    assert(mutableMap[0]=="x")
    mutableMap[0]=null
    assert(mutableMap[0]==null)
}


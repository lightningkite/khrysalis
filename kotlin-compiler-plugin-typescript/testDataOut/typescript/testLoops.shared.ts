// Generated by Khrysalis TypeScript converter
// File: /home/josephivie/IdeaProjects/khrysalis/kotlin-compiler-plugin-typescript/testData/testLoops.shared.kt
// Package: com.test.loops
// Imported FQ name: com.test.loops.main.i TS i
// Imported FQ name: com.test.loops.main.item TS item
// Imported FQ name: com.test.loops.main.key TS key
// Imported FQ name: com.test.loops.main.value TS value
// Imported FQ name: kotlin.collections.Map.entries TS entries
// Imported FQ name: kotlin.collections.listOf TS listOf
// Imported FQ name: kotlin.collections.mapOf TS mapOf
// Imported FQ name: kotlin.io.println TS println
// Imported FQ name: kotlin.to TS KotlinAnyTo

export function main(){
    for (const item of listOf(1)) {
        println(item);
    }
    for (const toDestructure of mapOf(KotlinAnyTo(1, 2)).entries) {
        const key = toDestructure[0]
        const value = toDestructure[1]
        
        println(`${key}: ${value}`)
        
    }
    let i = 0;
    
    while(i.compareTo(4)){
        i++;
        println(i);
    }
    label: while(i.compareTo(6)){
        i++;
        println(i);
        break label;
    };
    do {
        i++;
        println(i);
    } while(i.compareTo(8))
}

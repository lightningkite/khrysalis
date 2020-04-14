//Package: com.test
//Converted using Khrysalis2


export function main(): void {
    const lambda : () => void = { word1 , word2 -> 
         console.log("${word1} ${word2}!")
     };
    lambda("Hello", "world")
}
 

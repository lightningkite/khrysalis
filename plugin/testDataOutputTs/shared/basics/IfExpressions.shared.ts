//Package: com.test
//Converted using Khrysalis2


export function main(): void {
    let aNumber : number = 0 
    ;
    aNumber += if ( true ) 1 else 0;
    const letCheckValue : string | null = null;
    aNumber += letCheckValue?.let{ 2
     } ?? 0;
    console.log(aNumber)
}
 

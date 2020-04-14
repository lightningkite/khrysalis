//Package: com.test
//Converted using Khrysalis2


export function main(): void {
    console.log(addNumbers(1, addNumbers2(2, addNumbers3(3, 4))))
}
 
 

export function addNumbers(left: number, right: number): number {
    return left + right;
}
 

export function addNumbers2(left: number, right: number): number {
    return left + right
}
 
 

export function addNumbers3(left: number, right: number): number {
    subfunction(left: number): number {
        return left + right
    }
    ;
    return subfunction(left)
}
 

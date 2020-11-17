
import { hashString } from 'butterfly/dist/Kotlin'

function test(){
    hashString("Hello!")
}

//! Declares com.test.sayDelicious
export function sayDelicious() {
    console.log("Delicious Typescript!")
}
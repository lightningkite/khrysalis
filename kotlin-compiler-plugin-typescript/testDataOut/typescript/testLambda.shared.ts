// Generated by Khrysalis TypeScript converter
// File: /home/josephivie/IdeaProjects/khrysalis/kotlin-compiler-plugin-typescript/testData/testLambda.shared.kt
// Package: com.test.lambda
// Imported FQ name: com.test.lambda.TestClass SKIPPED due to same file
// Imported FQ name: com.test.lambda.TestClass TS TestClass
// Imported FQ name: com.test.lambda.TestClass.item TS item
// Imported FQ name: com.test.lambda.main.<anonymous>.it TS it
// Imported FQ name: com.test.lambda.main.theAnswer TS theAnswer
import { also } from 'khrysalis/dist/Kotlin'

export class TestClass {
    
    public item: number = 0;
    
}

export function main(){
    const theAnswer = also(new TestClass(), (this_) => this_.item = 42);
    
    const myLambda: (a: number) => string = (it) => `Number: ${it}`;
    
    ((it) => console.log(it))(theAnswer);
    ((it) => {})(32);
    ((it) => console.log(it))(32);
    ((it) => {
            console.log(it);
            console.log(it);
    })(32);
}

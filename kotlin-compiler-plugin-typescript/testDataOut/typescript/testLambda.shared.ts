// Generated by Khrysalis TypeScript converter
// File: /home/josephivie/IdeaProjects/khrysalis/kotlin-compiler-plugin-typescript/testData/testLambda.shared.kt
// Package: com.test.lambda
// Imported FQ name: com.test.lambda.TestClass SKIPPED due to same file
// Imported FQ name: com.test.lambda.TestClass TS TestClass
// Imported FQ name: com.test.lambda.TestClass.item TS item
// Imported FQ name: com.test.lambda.main.<anonymous>.it TS it
// Imported FQ name: com.test.lambda.main.theAnswer TS theAnswer
// Imported FQ name: kotlin.Int TS Int
// Imported FQ name: kotlin.String TS String
// Imported FQ name: kotlin.apply TS KotlinAnyApply
// Imported FQ name: kotlin.io.println TS println
// Imported FQ name: kotlin.let TS KotlinAnyLet

export class TestClass {
    
    public item: Int = 0;
    
}

export function main(){
    const theAnswer = KotlinAnyApply(new TestClass(), (this_) => this_.item = 42);
    
    const myLambda: (a: Int) => String = (it) => `Number: ${it}`;
    
    KotlinAnyLet(theAnswer, (it) => println(it));
    KotlinAnyLet(32, (it) => {});
    KotlinAnyLet(32, (it) => println(it));
    KotlinAnyLet(32, (it) => {
            println(it);
            println(it);
    });
}

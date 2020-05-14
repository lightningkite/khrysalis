// Generated by Khrysalis TypeScript converter
// File: /home/josephivie/IdeaProjects/khrysalis/kotlin-compiler-plugin-typescript/testData/testVariables.shared.kt
// Package: com.test.variables
import { GenericTest, TestClass, TestObject, getComTestVariablesTestClassExtensionProperty, getTopLevelHybrid, getTopLevelReal, getTopLevelVirtual, setComTestVariablesTestClassExtensionProperty, setTopLevelHybrid, setTopLevelReal, setTopLevelVirtual } from './testVariables.shared'

let fileReal: number = 0;

//! Declares com.test.variables.topLevelReal
export let _topLevelReal: number = 0;
export function getTopLevelReal(): number { return _topLevelReal; }
export function setTopLevelReal(value: number) { _topLevelReal = value; }

//! Declares com.test.variables.topLevelVirtual
export function getTopLevelVirtual(): number { return 1; }
export function setTopLevelVirtual(value: number) {
    console.log(`Attempted to set ${value}`);
}

//! Declares com.test.variables.topLevelHybrid
export let _topLevelHybrid: number = 2;
export function getTopLevelHybrid(): number { return _topLevelHybrid; }
export function setTopLevelHybrid(value: number) {
    _topLevelHybrid = value + 1;
}


//! Declares com.test.variables.topLevelUsage
export function topLevelUsage(){
    setTopLevelReal(-1);
    console.log(getTopLevelReal());
    setTopLevelVirtual(-2);
    setTopLevelVirtual(getTopLevelVirtual() + 3);
    console.log(getTopLevelVirtual());
    setTopLevelHybrid(-3);
    console.log(getTopLevelHybrid());
}

//! Declares com.test.variables.TestClass
export class TestClass {
    
    public memberReal: number = 0;
    
    //! Declares com.test.variables.TestClass.memberVirtual
    public get memberVirtual(): number { return 1; }
    public set memberVirtual(value: number) {
        console.log(`Attempted to set ${value}`);
    }
    
    public _memberHybrid: number = 2;
    public get memberHybrid(): number { return this._memberHybrid; }
    public set memberHybrid(value: number) {
        this._memberHybrid = value + 1;
    }
    
    public memberUsage(){
        this.memberReal = -1;
        console.log(this.memberReal);
        this.memberVirtual = -2;
        console.log(this.memberVirtual);
        this.memberHybrid = -3;
        console.log(this.memberHybrid);
        setComTestVariablesTestClassExtensionProperty(this, -4);
        console.log(getComTestVariablesTestClassExtensionProperty(this));
        setComTestVariablesTestClassExtensionProperty(this, -4);
        console.log(getComTestVariablesTestClassExtensionProperty(this));
        
        setTopLevelReal(-1);
        console.log(getTopLevelReal());
        setTopLevelVirtual(-2);
        console.log(getTopLevelVirtual());
        setTopLevelHybrid(-3);
        console.log(getTopLevelHybrid());
        
        TestClass.Companion.INSTANCE.companionReal = -1;
        console.log(TestClass.Companion.INSTANCE.companionReal);
        TestClass.Companion.INSTANCE.companionVirtual = -2;
        console.log(TestClass.Companion.INSTANCE.companionVirtual);
        TestClass.Companion.INSTANCE.companionHybrid = -3;
        console.log(TestClass.Companion.INSTANCE.companionHybrid);
        
        TestClass.Companion.INSTANCE.companionReal = -1;
        console.log(TestClass.Companion.INSTANCE.companionReal);
        TestClass.Companion.INSTANCE.companionVirtual = -2;
        console.log(TestClass.Companion.INSTANCE.companionVirtual);
        TestClass.Companion.INSTANCE.companionHybrid = -3;
        console.log(TestClass.Companion.INSTANCE.companionHybrid);
        
        TestClass.Companion.INSTANCE.companionReal = -1;
        console.log(TestClass.Companion.INSTANCE.companionReal);
        TestClass.Companion.INSTANCE.companionVirtual = -2;
        console.log(TestClass.Companion.INSTANCE.companionVirtual);
        TestClass.Companion.INSTANCE.companionHybrid = -3;
        console.log(TestClass.Companion.INSTANCE.companionHybrid);
    }
    
    public static Companion = class Companion {
        private constructor() {
            this.companionReal = 0;
            this.companionHybrid = 2;
        }
        public static INSTANCE = new Companion();
        
        public companionReal: number = 0;
        
        //! Declares com.test.variables.TestClass.Companion.companionVirtual
        public get companionVirtual(): number { return 1; }
        public set companionVirtual(value: number) {
            console.log(`Attempted to set ${value}`);
        }
        
        public _companionHybrid: number = 2;
        public get companionHybrid(): number { return this._companionHybrid; }
        public set companionHybrid(value: number) {
            this._companionHybrid = value + 1;
        }
        
    }
}

//! Declares com.test.variables.extensionProperty
export function getComTestVariablesTestClassExtensionProperty(this_ExtensionProperty: TestClass): number { return this_ExtensionProperty.memberReal; }
export function setComTestVariablesTestClassExtensionProperty(this_ExtensionProperty: TestClass, value: number) {
    this_ExtensionProperty.memberReal = value;
}


//! Declares com.test.variables.TestObject
export class TestObject {
    private constructor() {
        this.objectReal = 0;
        this.objectHybrid = 2;
    }
    public static INSTANCE = new TestObject();
    
    public objectReal: number = 0;
    
    //! Declares com.test.variables.TestObject.objectVirtual
    public get objectVirtual(): number { return 1; }
    public set objectVirtual(value: number) {
        console.log(`Attempted to set ${value}`);
    }
    
    public _objectHybrid: number = 2;
    public get objectHybrid(): number { return this._objectHybrid; }
    public set objectHybrid(value: number) {
        this._objectHybrid = value + 1;
    }
    
    objectUsage(){
        this.objectReal = -1;
        console.log(this.objectReal);
        this.objectVirtual = -2;
        console.log(this.objectVirtual);
        this.objectHybrid = -3;
        console.log(this.objectHybrid);
        
        setTopLevelReal(-1);
        console.log(getTopLevelReal());
        setTopLevelVirtual(-2);
        console.log(getTopLevelVirtual());
        setTopLevelHybrid(-3);
        console.log(getTopLevelHybrid());
        
        const testInstance = new TestClass();
        
        this.setComTestVariablesTestClassNeedlesslyComplex(testInstance, -4);
        console.log(this.getComTestVariablesTestClassNeedlesslyComplex(testInstance));
    }
    //! Declares com.test.variables.TestObject.needlesslyComplex
    getComTestVariablesTestClassNeedlesslyComplex(this_NeedlesslyComplex: TestClass): number { return this_NeedlesslyComplex.memberReal; }
    setComTestVariablesTestClassNeedlesslyComplex(this_NeedlesslyComplex: TestClass, value: number) {
        this_NeedlesslyComplex.memberReal = value;
        this.objectReal = value;
    }
    
}

//! Declares com.test.variables.GenericTest
export class GenericTest<T> {
    
}
//! Declares com.test.variables.ext
export function getComTestVariablesGenericTestExt<T>(this_Ext: GenericTest<T>): number { return 1; }


//! Declares com.test.variables.extensionProperty
export function getComTestVariablesTestObjectExtensionProperty(this_ExtensionProperty: TestObject): number { return this_ExtensionProperty.objectReal; }
export function setComTestVariablesTestObjectExtensionProperty(this_ExtensionProperty: TestObject, value: number) {
    this_ExtensionProperty.objectReal = value;
}


//! Declares com.test.variables.test
export function test(){
    const instance = new TestClass();
    
    fileReal = fileReal + 1;
    console.log(TestObject.INSTANCE.objectReal);
    console.log(instance.memberReal);
    console.log(getComTestVariablesTestClassExtensionProperty(instance));
    console.log(42 /*magicVariable get!*/);
    console.log(`Setting magicVariable to ${Math.abs(9001) + 4}`);;
}

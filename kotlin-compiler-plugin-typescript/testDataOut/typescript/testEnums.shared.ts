// Generated by Khrysalis TypeScript converter
// File: /home/josephivie/IdeaProjects/khrysalis/kotlin-compiler-plugin-typescript/testData/testEnums.shared.kt
// Package: com.test.classes
// Imported FQ name: com.test.classes.AdvancedSuits SKIPPED due to same file
// Imported FQ name: com.test.classes.AdvancedSuits TS AdvancedSuits
// Imported FQ name: com.test.classes.AdvancedSuits.DIAMONDS TS DIAMONDS
// Imported FQ name: com.test.classes.AdvancedSuits.SPADES.print.cardNum TS cardNum
// Imported FQ name: com.test.classes.AdvancedSuits.name TS name
// Imported FQ name: com.test.classes.AdvancedSuits.print TS print
// Imported FQ name: com.test.classes.AdvancedSuits.print.cardNum TS cardNum
// Imported FQ name: com.test.classes.AdvancedSuits.valueOf TS valueOf
// Imported FQ name: com.test.classes.AdvancedSuits.values TS values
// Imported FQ name: com.test.classes.Suits SKIPPED due to same file
// Imported FQ name: com.test.classes.Suits TS Suits
// Imported FQ name: com.test.classes.Suits.CLUBS TS CLUBS
// Imported FQ name: com.test.classes.Suits.name TS name
// Imported FQ name: com.test.classes.Suits.valueOf TS valueOf
// Imported FQ name: com.test.classes.Suits.values TS values
// Imported FQ name: com.test.classes.testEnums.simp TS simp
// Imported FQ name: kotlin.Boolean TS Boolean

export class Suits {
    constructor(name: string) { this.name = name; }
    
    public static SPADES = new Suits("SPADES");
    public static CLUBS = new Suits("CLUBS");
    public static DIAMONDS = new Suits("DIAMONDS");
    public static HEARTS = new Suits("HEARTS");
    
    private static _values: Array<Suits> = [Suits.SPADES, Suits.CLUBS, Suits.DIAMONDS, Suits.HEARTS];
    public static values(): Array<Suits> { return Suits._values; }
    public readonly name: string;
    public static valueOf(name: string): Suits { return (Suits as any)[name]; }
    public toString(): string { return this.name }
}

export class AdvancedSuits {
    public readonly black: Boolean;
    private constructor(name: string,  black: Boolean) {
        this.name = name;
        this.black = black;
    }
    
    public static SPADES = new class SPADES extends AdvancedSuits {
        public constructor() {
            super("SPADES", true);
        }
        
        public print(cardNum: number){
            console.log(`♠${cardNum}`);
        }
    }();
    
    public static CLUBS = new AdvancedSuits("CLUBS", true);
    
    public static DIAMONDS = new AdvancedSuits("DIAMONDS", false);
    
    public static HEARTS = new AdvancedSuits("HEARTS", false);
    
    
    public print(cardNum: number){
        console.log(`${this}${cardNum}`);
    }
    private static _values: Array<AdvancedSuits> = [AdvancedSuits.SPADES, AdvancedSuits.CLUBS, AdvancedSuits.DIAMONDS, AdvancedSuits.HEARTS];
    public static values(): Array<AdvancedSuits> { return AdvancedSuits._values; }
    public readonly name: string;
    public static valueOf(name: string): AdvancedSuits { return (AdvancedSuits as any)[name]; }
    public toString(): string { return this.name }
}

export function testEnums(){
    const simpleSuit = Suits.CLUBS;
    
    const advancedSuit = AdvancedSuits.DIAMONDS;
    
    for (const simp of Suits.values()) {
        console.log(simp.name);
        console.log(Suits.valueOf(simp.name));
    }
    for (const simp of AdvancedSuits.values()) {
        console.log(simp.name);
        simp.print(3);
        console.log(AdvancedSuits.valueOf(simp.name));
    }
}

// Generated by Khrysalis TypeScript converter - this file will be overwritten.
// File: testEnums.shared.kt
// Package: com.test.classes

//! Declares com.test.classes.Suits
export class Suits {
    private constructor(name: string, jsonName: string) {
        this.name = name;
        this.jsonName = jsonName;
    }
    
    public static SPADES = new Suits("SPADES", "SPADES");
    public static CLUBS = new Suits("CLUBS", "CLUBS");
    public static DIAMONDS = new Suits("DIAMONDS", "DIAMONDS");
    public static HEARTS = new Suits("HEARTS", "HEARTS");
    
    private static _values: Array<Suits> = [Suits.SPADES, Suits.CLUBS, Suits.DIAMONDS, Suits.HEARTS];
    public static values(): Array<Suits> { return Suits._values; }
    public readonly name: string;
    public readonly jsonName: string;
    public static valueOf(name: string): Suits { return (Suits as any)[name]; }
    public toString(): string { return this.name }
    public toJSON(): string { return this.jsonName }
}

//! Declares com.test.classes.AdvancedSuits
export class AdvancedSuits {
    public readonly black: Boolean;
    private constructor(name: string, jsonName: string, black: Boolean) {
        this.name = name;
        this.jsonName = jsonName;
        this.black = black;
    }
    
    public static SPADES = new class SPADES extends AdvancedSuits {
        public constructor() {
            super("SPADES", "SPADES", true);
        }
        
        public print(cardNum: Int): void {
            println(`♠${cardNum}`);
        }
    }();
    
    public static CLUBS = new AdvancedSuits("CLUBS", "CLUBS", true);
    
    public static DIAMONDS = new AdvancedSuits("DIAMONDS", "DIAMONDS", false);
    
    public static HEARTS = new AdvancedSuits("HEARTS", "HEARTS", false);
    
    
    public print(cardNum: Int): void {
        println(`${this}${cardNum}`);
    }
    private static _values: Array<AdvancedSuits> = [AdvancedSuits.SPADES, AdvancedSuits.CLUBS, AdvancedSuits.DIAMONDS, AdvancedSuits.HEARTS];
    public static values(): Array<AdvancedSuits> { return AdvancedSuits._values; }
    public readonly name: string;
    public readonly jsonName: string;
    public static valueOf(name: string): AdvancedSuits { return (AdvancedSuits as any)[name]; }
    public toString(): string { return this.name }
    public toJSON(): string { return this.jsonName }
}

//! Declares com.test.classes.StatusEnum
export class StatusEnum implements Codable {
    public static implementsInterface = true;
    public readonly comparableValue: Int;
    public readonly darkColorResource: Int;
    public readonly colorResource: Int;
    public readonly textResource: Int;
    private constructor(name: string, jsonName: string, comparableValue: Int, darkColorResource: Int = 0, colorResource: Int = 0, textResource: Int = 0) {
        this.name = name;
        this.jsonName = jsonName;
        this.comparableValue = comparableValue;
        this.darkColorResource = darkColorResource;
        this.colorResource = colorResource;
        this.textResource = textResource;
    }
    
    public static Safe = new StatusEnum("Safe", "safe", 3, 0, 0, 0);
    
    public static Unsafe = new StatusEnum("Unsafe", "unsafe", 1, undefined, 0, 0);
    
    public static Cleared = new StatusEnum("Cleared", "cleared", 4, 0, undefined, 0);
    
    public static Unknown = new StatusEnum("Unknown", "unknown", 2, 0, 0, undefined);
    
    private static _values: Array<StatusEnum> = [StatusEnum.Safe, StatusEnum.Unsafe, StatusEnum.Cleared, StatusEnum.Unknown];
    public static values(): Array<StatusEnum> { return StatusEnum._values; }
    public readonly name: string;
    public readonly jsonName: string;
    public static valueOf(name: string): StatusEnum { return (StatusEnum as any)[name]; }
    public toString(): string { return this.name }
    public toJSON(): string { return this.jsonName }
}

//! Declares com.test.classes.testEnums
export function testEnums(): void {
    const simpleSuit = Suits.CLUBS;
    
    const advancedSuit = AdvancedSuits.DIAMONDS;
    
    for (const simp of Suits.values()) {
        println(simp.name);
        println(Suits.valueOf(simp.name));
    }
    for (const simp of AdvancedSuits.values()) {
        println(simp.name);
        simp.print(3);
        println(AdvancedSuits.valueOf(simp.name));
    }
}
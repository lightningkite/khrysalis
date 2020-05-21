// Generated by Khrysalis TypeScript converter - this file will be overwritten.
// File: views/geometry/Align.shared.kt
// Package: com.lightningkite.khrysalis.views.geometry
// FQImport: com.lightningkite.khrysalis.views.geometry.Align.fill TS fill
// FQImport: com.lightningkite.khrysalis.views.geometry.AlignPair TS AlignPair
// FQImport: com.lightningkite.khrysalis.views.geometry.Align.center TS center
// FQImport: com.lightningkite.khrysalis.views.geometry.AlignPair SKIPPED due to same file
// FQImport: com.lightningkite.khrysalis.views.geometry.Align.end TS end
// FQImport: com.lightningkite.khrysalis.views.geometry.Align SKIPPED due to same file
// FQImport: com.lightningkite.khrysalis.views.geometry.Align.start TS start
// FQImport: com.lightningkite.khrysalis.views.geometry.Align TS Align

//! Declares com.lightningkite.khrysalis.views.geometry.Align
export class Align {
    constructor(name: string) { this.name = name; }
    
    public static start = new Align("start");
    public static center = new Align("center");
    public static end = new Align("end");
    public static fill = new Align("fill");
    
    private static _values: Array<Align> = [Align.start, Align.center, Align.end, Align.fill];
    public static values(): Array<Align> { return Align._values; }
    public readonly name: string;
    public static valueOf(name: string): Align { return (Align as any)[name]; }
    public toString(): string { return this.name }
}

//! Declares com.lightningkite.khrysalis.views.geometry.AlignPair
export class AlignPair {
    public readonly horizontal: Align;
    public readonly vertical: Align;
    public constructor(horizontal: Align, vertical: Align) {
        this.horizontal = horizontal;
        this.vertical = vertical;
    }
    public hashCode(): number {
        let hash = 17;
        hash = 31 * hash + this.horizontal?.hashCode() ?? 0;
        hash = 31 * hash + this.vertical?.hashCode() ?? 0;
        return hash;
    }
    public equals(other: any): boolean { return other instanceof AlignPair && (this.horizontal?.equals(other.horizontal) ?? other.horizontal === null) && (this.vertical?.equals(other.vertical) ?? other.vertical === null) }
    public toString(): string { return `AlignPair(horizontal = ${this.horizontal}, vertical = ${this.vertical})` }
    public copy(horizontal: Align = this.horizontal, vertical: Align = this.vertical) { return new AlignPair(horizontal, vertical); }
    
    
    public static Companion = class Companion {
        private constructor() {
            this.center = new AlignPair(Align.center, Align.center);
            this.fill = new AlignPair(Align.fill, Align.fill);
            this.topLeft = new AlignPair(Align.start, Align.start);
            this.topCenter = new AlignPair(Align.center, Align.start);
            this.topFill = new AlignPair(Align.fill, Align.start);
            this.topRight = new AlignPair(Align.end, Align.start);
            this.centerLeft = new AlignPair(Align.start, Align.center);
            this.centerCenter = new AlignPair(Align.center, Align.center);
            this.centerFill = new AlignPair(Align.fill, Align.center);
            this.centerRight = new AlignPair(Align.end, Align.center);
            this.fillLeft = new AlignPair(Align.start, Align.fill);
            this.fillCenter = new AlignPair(Align.center, Align.fill);
            this.fillFill = new AlignPair(Align.fill, Align.fill);
            this.fillRight = new AlignPair(Align.end, Align.fill);
            this.bottomLeft = new AlignPair(Align.start, Align.end);
            this.bottomCenter = new AlignPair(Align.center, Align.end);
            this.bottomFill = new AlignPair(Align.fill, Align.end);
            this.bottomRight = new AlignPair(Align.end, Align.end);
        }
        public static INSTANCE = new Companion();
        
        public readonly center;
        
        public readonly fill;
        
        
        public readonly topLeft;
        
        public readonly topCenter;
        
        public readonly topFill;
        
        public readonly topRight;
        
        public readonly centerLeft;
        
        public readonly centerCenter;
        
        public readonly centerFill;
        
        public readonly centerRight;
        
        public readonly fillLeft;
        
        public readonly fillCenter;
        
        public readonly fillFill;
        
        public readonly fillRight;
        
        public readonly bottomLeft;
        
        public readonly bottomCenter;
        
        public readonly bottomFill;
        
        public readonly bottomRight;
        
    }
    
}


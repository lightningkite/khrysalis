export declare class Align {
    private constructor();
    static start: Align;
    static center: Align;
    static end: Align;
    static fill: Align;
    private static _values;
    static values(): Array<Align>;
    readonly name: string;
    readonly jsonName: string;
    static valueOf(name: string): Align;
    toString(): string;
    toJSON(): string;
}
export declare class AlignPair {
    readonly horizontal: Align;
    readonly vertical: Align;
    constructor(horizontal: Align, vertical: Align);
    hashCode(): number;
    equals(other: any): boolean;
    toString(): string;
    copy(horizontal?: Align, vertical?: Align): AlignPair;
}
export declare namespace AlignPair {
    class Companion {
        private constructor();
        static INSTANCE: Companion;
        readonly center: AlignPair;
        readonly fill: AlignPair;
        readonly topLeft: AlignPair;
        readonly topCenter: AlignPair;
        readonly topFill: AlignPair;
        readonly topRight: AlignPair;
        readonly centerLeft: AlignPair;
        readonly centerCenter: AlignPair;
        readonly centerFill: AlignPair;
        readonly centerRight: AlignPair;
        readonly fillLeft: AlignPair;
        readonly fillCenter: AlignPair;
        readonly fillFill: AlignPair;
        readonly fillRight: AlignPair;
        readonly bottomLeft: AlignPair;
        readonly bottomCenter: AlignPair;
        readonly bottomFill: AlignPair;
        readonly bottomRight: AlignPair;
    }
}

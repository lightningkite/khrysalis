export declare class DrawableResource {
    cssClass: string;
    filePath?: string;
    constructor(cssClass: string, filePath?: string);
    hashCode(): number;
    equals(other: any): boolean;
    toString(): string;
}

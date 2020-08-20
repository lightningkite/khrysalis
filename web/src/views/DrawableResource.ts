
import {hashAnything, safeEq} from "../kotlin/Language";

//! Declares com.lightningkite.khrysalis.views.DrawableResource
export class DrawableResource {
    public cssClass: string;
    public filePath?: string;
    public constructor(cssClass: string, filePath?: string) {
        this.cssClass = cssClass;
        this.filePath = filePath;
    }
    public hashCode(): number {
        let hash = 17;
        hash = 31 * hash + hashAnything(this.cssClass);
        return hash;
    }
    public equals(other: any): boolean { return other instanceof DrawableResource && safeEq(this.cssClass, other.cssClass) }
    public toString(): string { return `DrawableResource(cssClass = ${this.cssClass})` }
}
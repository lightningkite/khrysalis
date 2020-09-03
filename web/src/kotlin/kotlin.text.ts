
//! Declares kotlin.text.isBlank
export function xCharSequenceIsBlank(s: string | null): boolean {
    if(s == null) return true;
    for(const c of s) {
        if(!xCharIsWhitespace(c)){
            return false
        }
    }
    return true
}

//! Declares kotlin.text.substringBefore
export function xStringSubstringBefore(s: string, delimeter: string, defaultResult: string = s): string {
    const pos = s.indexOf(delimeter);
    if(pos == -1) {
        return defaultResult
    } else {
        return s.substring(0, pos)
    }
}

//! Declares kotlin.text.substringAfter
export function xStringSubstringAfter(s: string, delimeter: string, defaultResult: string = s): string {
    const pos = s.indexOf(delimeter);
    if(pos == -1) {
        return defaultResult
    } else {
        return s.substring(pos + delimeter.length)
    }
}

//! Declares kotlin.text.substringBeforeLast
export function xStringSubstringBeforeLast(s: string, delimeter: string, defaultResult: string = s): string {
    const pos = s.lastIndexOf(delimeter);
    if(pos == -1) {
        return defaultResult
    } else {
        return s.substring(0, pos)
    }
}

//! Declares kotlin.text.substringAfterLast
export function xStringSubstringAfterLast(s: string, delimeter: string, defaultResult: string = s): string {
    const pos = s.lastIndexOf(delimeter);
    if(pos == -1) {
        return defaultResult
    } else {
        return s.substring(pos + delimeter.length)
    }
}

//! Declares kotlin.text.trimIndent
export function xStringTrimIndent(c: string): string {
    return c.split('\n').map((x)=>x.trim()).join("")
}

//! Declares kotlin.text.isWhitespace
export function xCharIsWhitespace(c: string): boolean {
    switch(c){
        case " ":
        case "\n":
        case "\r":
        case "\t":
            return true
        default:
            return false
    }
}

//! Declares kotlin.text.isUpperCase
export function xCharIsUpperCase(c: string): boolean {
    return c.toUpperCase() === c
}

//! Declares kotlin.text.isLowerCase
export function xCharIsLowerCase(c: string): boolean {
    return c.toLowerCase() === c
}

//! Declares kotlin.text.isLetter
export function xCharIsLetter(c: string): boolean {
    return (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z')
}

//! Declares kotlin.text.isDigit
export function xCharIsDigit(c: string): boolean {
    return c >= '0' && c <= '9'
}

//! Declares kotlin.text.isLetterOrDigit
export function xCharIsLetterOrDigit(c: string): boolean {
    return xCharIsLetter(c) || xCharIsDigit(c)
}

//! Declares kotlin.text.StringBuilder
//! Declares java.lang.StringBuilder
export class StringBuilder {
    value: string = ""
    toString(): string { return this.value }
}

//! Declares kotlin.text.indexOfAny
export function xCharSequenceIndexOfAny(c: string, set: Array<string>, start: number, caseSensitive: boolean): number {
    if(caseSensitive){
        c = c.toLowerCase();
        set = set.map((x)=>x.toLowerCase());
    }
    let lowest = c.length;
    for(const s of set){
        const result = c.indexOf(s, start);
        if(result != -1 && result < lowest) {
            lowest = result;
        }
    }
    return lowest == c.length ? -1 : lowest;
}
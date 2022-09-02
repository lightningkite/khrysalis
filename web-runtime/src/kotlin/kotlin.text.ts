
export function xCharSequenceIsBlank(s: string | null): boolean {
    if(s == null) return true;
    for(const c of s) {
        if(!xCharIsWhitespace(c)){
            return false
        }
    }
    return true
}

// export function substring(s: string, findIndex: (this: string, delimiter: string)=>number, after: boolean, delimiter: string, defaultResult: string = s): string {
//     const pos = findIndex.bind(s)(delimiter)
//     return pos == -1 ? defaultResult : after ? s.substring(pos + delimiter.length) : s.substring(0, pos)
// }
// function quickTest() {
//     substring("asfd", String.prototype.indexOf, true, "f", "x")
// }

export function xStringReplaceFirstChar(s: string, action: (s: string)=>string): string {
    if(s.length === 0) return s
    return action(s.charAt(0)) + s.substring(1)
}

export function xStringSubstringBefore(s: string, delimiter: string, defaultResult: string = s): string {
    const pos = s.indexOf(delimiter);
    return pos == -1 ? defaultResult : s.substring(0, pos)
}

export function xStringSubstringAfter(s: string, delimiter: string, defaultResult: string = s): string {
    const pos = s.indexOf(delimiter);
    return pos == -1 ? defaultResult : s.substring(pos + delimiter.length)
}

export function xStringSubstringBeforeLast(s: string, delimiter: string, defaultResult: string = s): string {
    const pos = s.lastIndexOf(delimiter);
    return pos == -1 ? defaultResult : s.substring(0, pos)
}

export function xStringSubstringAfterLast(s: string, delimiter: string, defaultResult: string = s): string {
    const pos = s.lastIndexOf(delimiter);
    return pos == -1 ? defaultResult : s.substring(pos + delimiter.length)
}

export function xStringTrimIndent(c: string): string {
    return c.split('\n').map((x)=>x.trim()).join("")
}

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

export function xCharIsUpperCase(c: string): boolean {
    return c.toUpperCase() === c
}

export function xCharIsLowerCase(c: string): boolean {
    return c.toLowerCase() === c
}

export function xCharIsLetter(c: string): boolean {
    return (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z')
}

export function xCharIsDigit(c: string): boolean {
    return c >= '0' && c <= '9'
}

export function xCharIsLetterOrDigit(c: string): boolean {
    return xCharIsLetter(c) || xCharIsDigit(c)
}

export class StringBuilder {
    value: string = ""
    toString(): string { return this.value }
}
export function buildString(block: (this_: StringBuilder)=>void): string {
    const b = new StringBuilder()
    block(b)
    return b.toString()
}

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

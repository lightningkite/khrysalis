"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
//! Declares kotlin.text.isBlank
function kotlinCharSequenceIsBlank(s) {
    if (s == null)
        return true;
    for (const c of s) {
        if (!kotlinCharIsWhitespace(c)) {
            return false;
        }
    }
    return true;
}
exports.kotlinCharSequenceIsBlank = kotlinCharSequenceIsBlank;
//! Declares kotlin.text.substringBefore
function kotlinStringSubstringBefore(s, delimeter, defaultResult = s) {
    const pos = s.indexOf(delimeter);
    if (pos == -1) {
        return defaultResult;
    }
    else {
        return s.substring(0, pos);
    }
}
exports.kotlinStringSubstringBefore = kotlinStringSubstringBefore;
//! Declares kotlin.text.substringAfter
function kotlinStringSubstringAfter(s, delimeter, defaultResult = s) {
    const pos = s.indexOf(delimeter);
    if (pos == -1) {
        return defaultResult;
    }
    else {
        return s.substring(pos + delimeter.length);
    }
}
exports.kotlinStringSubstringAfter = kotlinStringSubstringAfter;
//! Declares kotlin.text.substringBeforeLast
function kotlinStringSubstringBeforeLast(s, delimeter, defaultResult = s) {
    const pos = s.lastIndexOf(delimeter);
    if (pos == -1) {
        return defaultResult;
    }
    else {
        return s.substring(0, pos);
    }
}
exports.kotlinStringSubstringBeforeLast = kotlinStringSubstringBeforeLast;
//! Declares kotlin.text.substringAfterLast
function kotlinStringSubstringAfterLast(s, delimeter, defaultResult = s) {
    const pos = s.lastIndexOf(delimeter);
    if (pos == -1) {
        return defaultResult;
    }
    else {
        return s.substring(pos + delimeter.length);
    }
}
exports.kotlinStringSubstringAfterLast = kotlinStringSubstringAfterLast;
//! Declares kotlin.text.trimIndent
function kotlinStringTrimIndent(c) {
    return c.split('\n').map((x) => x.trim()).join("");
}
exports.kotlinStringTrimIndent = kotlinStringTrimIndent;
//! Declares kotlin.text.isWhitespace
function kotlinCharIsWhitespace(c) {
    switch (c) {
        case " ":
        case "\n":
        case "\r":
        case "\t":
            return true;
        default:
            return false;
    }
}
exports.kotlinCharIsWhitespace = kotlinCharIsWhitespace;
//! Declares kotlin.text.isUpperCase
function kotlinCharIsUpperCase(c) {
    return c.toUpperCase() === c;
}
exports.kotlinCharIsUpperCase = kotlinCharIsUpperCase;
//! Declares kotlin.text.isLowerCase
function kotlinCharIsLowerCase(c) {
    return c.toLowerCase() === c;
}
exports.kotlinCharIsLowerCase = kotlinCharIsLowerCase;
//! Declares kotlin.text.isLetter
function kotlinCharIsLetter(c) {
    return (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z');
}
exports.kotlinCharIsLetter = kotlinCharIsLetter;
//! Declares kotlin.text.isDigit
function kotlinCharIsDigit(c) {
    return c >= '0' && c <= '9';
}
exports.kotlinCharIsDigit = kotlinCharIsDigit;
//! Declares kotlin.text.isLetterOrDigit
function kotlinCharIsLetterOrDigit(c) {
    return kotlinCharIsLetter(c) || kotlinCharIsDigit(c);
}
exports.kotlinCharIsLetterOrDigit = kotlinCharIsLetterOrDigit;
//! Declares kotlin.text.StringBuilder
//! Declares java.lang.StringBuilder
class StringBuilder {
    constructor() {
        this.value = "";
    }
    toString() { return this.value; }
}
exports.StringBuilder = StringBuilder;
//! Declares kotlin.text.indexOfAny
function kotlinCharSequenceIndexOfAny(c, set, start, caseSensitive) {
    if (caseSensitive) {
        c = c.toLowerCase();
        set = set.map((x) => x.toLowerCase());
    }
    let lowest = c.length;
    for (const s of set) {
        const result = c.indexOf(s, start);
        if (result != -1 && result < lowest) {
            lowest = result;
        }
    }
    return lowest == c.length ? -1 : lowest;
}
exports.kotlinCharSequenceIndexOfAny = kotlinCharSequenceIndexOfAny;
//# sourceMappingURL=kotlin.text.js.map
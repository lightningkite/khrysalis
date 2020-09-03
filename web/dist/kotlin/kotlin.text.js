"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
//! Declares kotlin.text.isBlank
function xCharSequenceIsBlank(s) {
    if (s == null)
        return true;
    for (const c of s) {
        if (!xCharIsWhitespace(c)) {
            return false;
        }
    }
    return true;
}
exports.xCharSequenceIsBlank = xCharSequenceIsBlank;
//! Declares kotlin.text.substringBefore
function xStringSubstringBefore(s, delimeter, defaultResult = s) {
    const pos = s.indexOf(delimeter);
    if (pos == -1) {
        return defaultResult;
    }
    else {
        return s.substring(0, pos);
    }
}
exports.xStringSubstringBefore = xStringSubstringBefore;
//! Declares kotlin.text.substringAfter
function xStringSubstringAfter(s, delimeter, defaultResult = s) {
    const pos = s.indexOf(delimeter);
    if (pos == -1) {
        return defaultResult;
    }
    else {
        return s.substring(pos + delimeter.length);
    }
}
exports.xStringSubstringAfter = xStringSubstringAfter;
//! Declares kotlin.text.substringBeforeLast
function xStringSubstringBeforeLast(s, delimeter, defaultResult = s) {
    const pos = s.lastIndexOf(delimeter);
    if (pos == -1) {
        return defaultResult;
    }
    else {
        return s.substring(0, pos);
    }
}
exports.xStringSubstringBeforeLast = xStringSubstringBeforeLast;
//! Declares kotlin.text.substringAfterLast
function xStringSubstringAfterLast(s, delimeter, defaultResult = s) {
    const pos = s.lastIndexOf(delimeter);
    if (pos == -1) {
        return defaultResult;
    }
    else {
        return s.substring(pos + delimeter.length);
    }
}
exports.xStringSubstringAfterLast = xStringSubstringAfterLast;
//! Declares kotlin.text.trimIndent
function xStringTrimIndent(c) {
    return c.split('\n').map((x) => x.trim()).join("");
}
exports.xStringTrimIndent = xStringTrimIndent;
//! Declares kotlin.text.isWhitespace
function xCharIsWhitespace(c) {
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
exports.xCharIsWhitespace = xCharIsWhitespace;
//! Declares kotlin.text.isUpperCase
function xCharIsUpperCase(c) {
    return c.toUpperCase() === c;
}
exports.xCharIsUpperCase = xCharIsUpperCase;
//! Declares kotlin.text.isLowerCase
function xCharIsLowerCase(c) {
    return c.toLowerCase() === c;
}
exports.xCharIsLowerCase = xCharIsLowerCase;
//! Declares kotlin.text.isLetter
function xCharIsLetter(c) {
    return (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z');
}
exports.xCharIsLetter = xCharIsLetter;
//! Declares kotlin.text.isDigit
function xCharIsDigit(c) {
    return c >= '0' && c <= '9';
}
exports.xCharIsDigit = xCharIsDigit;
//! Declares kotlin.text.isLetterOrDigit
function xCharIsLetterOrDigit(c) {
    return xCharIsLetter(c) || xCharIsDigit(c);
}
exports.xCharIsLetterOrDigit = xCharIsLetterOrDigit;
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
function xCharSequenceIndexOfAny(c, set, start, caseSensitive) {
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
exports.xCharSequenceIndexOfAny = xCharSequenceIndexOfAny;
//# sourceMappingURL=kotlin.text.js.map
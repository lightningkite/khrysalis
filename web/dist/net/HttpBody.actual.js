"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
const HttpMediaType_actual_1 = require("./HttpMediaType.actual");
const jsonParsing_1 = require("./jsonParsing");
//! Declares com.lightningkite.khrysalis.net.HttpBody
class HttpBody {
    constructor(data, type) {
        this.data = data;
        this.type = type;
    }
}
exports.HttpBody = HttpBody;
//! Declares com.lightningkite.khrysalis.net.HttpBodyPart
class HttpBodyPart {
}
exports.HttpBodyPart = HttpBodyPart;
//! Declares com.lightningkite.khrysalis.net.toJsonHttpBody
function xAnyToJsonHttpBody(this_) {
    return new HttpBody(jsonParsing_1.stringify(this_), HttpMediaType_actual_1.HttpMediaTypes.INSTANCE.JSON);
}
exports.xAnyToJsonHttpBody = xAnyToJsonHttpBody;
//! Declares com.lightningkite.khrysalis.net.toHttpBody
function xByteArrayToHttpBody(this_, mediaType) {
    return new HttpBody(this_, mediaType);
}
exports.xByteArrayToHttpBody = xByteArrayToHttpBody;
//! Declares com.lightningkite.khrysalis.net.toHttpBody
function xStringToHttpBody(this_, mediaType = HttpMediaType_actual_1.HttpMediaTypes.INSTANCE.TEXT) {
    return new HttpBody(this_, mediaType);
}
exports.xStringToHttpBody = xStringToHttpBody;
//! Declares com.lightningkite.khrysalis.net.toHttpBody
//TODO: Figure out when we have bitmaps
// export function xBitmapToHttpBody(this_: Bitmap, maxBytes: number = 10_000_000): HttpBody{
//
// }
//! Declares com.lightningkite.khrysalis.net.multipartFormBody
function multipartFormBody(...parts) {
    var _a;
    const data = new FormData();
    for (const part of parts) {
        if (part.body != null) {
            data.append(part.name, part.body, (_a = part.filename) !== null && _a !== void 0 ? _a : "file");
        }
        else {
            data.append(part.name, part.value);
        }
    }
    return new HttpBody(data, HttpMediaType_actual_1.HttpMediaTypes.INSTANCE.MULTIPART_FORM_DATA);
}
exports.multipartFormBody = multipartFormBody;
//! Declares com.lightningkite.khrysalis.net.multipartFormFilePart
function multipartFormFilePart(name, valueOrFilename, body) {
    const result = new HttpBodyPart();
    result.name = name;
    if (body) {
        result.filename = valueOrFilename !== null && valueOrFilename !== void 0 ? valueOrFilename : null;
        result.body = body;
    }
    else {
        result.value = valueOrFilename !== null && valueOrFilename !== void 0 ? valueOrFilename : null;
    }
    return result;
}
exports.multipartFormFilePart = multipartFormFilePart;
//# sourceMappingURL=HttpBody.actual.js.map
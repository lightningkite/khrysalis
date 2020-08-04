"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
const HttpMediaType_actual_1 = require("./HttpMediaType.actual");
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
function kotlinAnyToJsonHttpBody(this_) {
    return new HttpBody(JSON.stringify(this), HttpMediaType_actual_1.HttpMediaTypes.INSTANCE.JSON);
}
exports.kotlinAnyToJsonHttpBody = kotlinAnyToJsonHttpBody;
//! Declares com.lightningkite.khrysalis.net.toHttpBody
function kotlinByteArrayToHttpBody(this_, mediaType) {
    return new HttpBody(this_, mediaType);
}
exports.kotlinByteArrayToHttpBody = kotlinByteArrayToHttpBody;
//! Declares com.lightningkite.khrysalis.net.toHttpBody
function kotlinStringToHttpBody(this_, mediaType = HttpMediaType_actual_1.HttpMediaTypes.INSTANCE.TEXT) {
    return new HttpBody(this_, mediaType);
}
exports.kotlinStringToHttpBody = kotlinStringToHttpBody;
//! Declares com.lightningkite.khrysalis.net.toHttpBody
//TODO: Figure out when we have bitmaps
// export function androidGraphicsBitmapToHttpBody(this_: Bitmap, maxBytes: number = 10_000_000): HttpBody{
//
// }
//! Declares com.lightningkite.khrysalis.net.multipartFormBody
function multipartFormBody(...parts) {
    const data = new FormData();
    for (const part of parts) {
        if (part.body != null) {
            data.append(part.name, part.body, part.filename);
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
        result.filename = valueOrFilename;
        result.body = body;
    }
    else {
        result.value = valueOrFilename;
    }
    return result;
}
exports.multipartFormFilePart = multipartFormFilePart;
//# sourceMappingURL=HttpBody.actual.js.map
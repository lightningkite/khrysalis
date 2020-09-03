"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
const HttpMediaType_actual_1 = require("./HttpMediaType.actual");
const jsonParsing_1 = require("./jsonParsing");
const rxjs_1 = require("rxjs");
const Image_loadingRx_actual_1 = require("../Image.loadingRx.actual");
const operators_1 = require("rxjs/operators");
const Language_1 = require("../kotlin/Language");
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
function xUriToHttpBody(this_) {
    return new HttpBody(this_, this_.type);
}
exports.xUriToHttpBody = xUriToHttpBody;
//! Declares com.lightningkite.khrysalis.net.toHttpBody
function xImageToHttpBody(this_, maxDimension = 2048) {
    return Image_loadingRx_actual_1.xImageLoad(this_).pipe(operators_1.flatMap((x) => resize(x, maxDimension)), operators_1.map((x) => new HttpBody(x, "image/png")));
}
exports.xImageToHttpBody = xImageToHttpBody;
function resize(image, maxDimension = 2048) {
    return new rxjs_1.Observable((em) => {
        try {
            let canvasElement = document.createElement("canvas");
            const wide = image.width > image.height;
            const tooBig = image.width > maxDimension || image.height > maxDimension;
            canvasElement.width = !tooBig ? image.width : (wide ? maxDimension : image.width / image.height * maxDimension);
            canvasElement.height = !tooBig ? image.height : (wide ? image.height / image.width * maxDimension : maxDimension);
            const ctx = canvasElement.getContext("2d");
            if (ctx) {
                ctx.clearRect(0, 0, canvasElement.width, canvasElement.height);
                ctx.drawImage(image, 0, 0);
            }
            canvasElement.toBlob((x) => {
                if (x) {
                    em.next(x);
                    em.complete();
                }
                else {
                    em.error(new Language_1.Exception("", undefined));
                }
            });
        }
        catch (e) {
            em.error(e);
        }
    });
}
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
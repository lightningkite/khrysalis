"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
// Generated by Khrysalis TypeScript converter
// File: views/ImageView.actual.kt
// Package: com.lightningkite.khrysalis.views
const Image_shared_1 = require("../Image.shared");
const delay_actual_1 = require("../delay.actual");
//! Declares com.lightningkite.khrysalis.observables.binding.loadImage>android.widget.ImageView
//! Declares com.lightningkite.khrysalis.views.loadImage>android.widget.ImageView
let canvasElement = document.createElement("canvas");
function androidWidgetImageViewLoadImage(this_, image) {
    delay_actual_1.post(() => {
        if (image instanceof Image_shared_1.ImageRaw) {
            const url = URL.createObjectURL(new Blob([image.raw]));
            this_.src = url;
        }
        else if (image instanceof Image_shared_1.ImageReference) {
            const reader = new FileReader();
            reader.onload = (e) => {
                const reader = e.target;
                if (reader !== null) {
                    this_.src = reader.result;
                }
            };
            reader.readAsDataURL(image.uri);
        }
        else if (image instanceof Image_shared_1.ImageImageBitmap) {
            canvasElement.width = image.bitmap.width;
            canvasElement.height = image.bitmap.height;
            const ctx = canvasElement.getContext("2d");
            if (ctx) {
                ctx.clearRect(0, 0, canvasElement.width, canvasElement.height);
                ctx.drawImage(image.bitmap, 0, 0);
            }
            canvasElement.toBlob((blob) => {
                const url = URL.createObjectURL(blob);
                this_.src = url;
            });
        }
        else if (image instanceof Image_shared_1.ImageRemoteUrl) {
            this_.src = image.url;
        }
    });
}
exports.androidWidgetImageViewLoadImage = androidWidgetImageViewLoadImage;
//# sourceMappingURL=ImageView.actual.js.map
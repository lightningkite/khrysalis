// Generated by Khrysalis TypeScript converter
// File: views/ImageView.actual.kt
// Package: com.lightningkite.khrysalis.views
import {Image, ImageImageBitmap, ImageRaw, ImageReference, ImageRemoteUrl, ImageResource} from '../Image.shared'
import {post} from "../delay.actual";
import {setViewBackgroundClass} from "./View.ext.actual";
import {DrawableResource} from "./DrawableResource";


//! Declares com.lightningkite.khrysalis.observables.binding.loadImage>android.widget.ImageView
//! Declares com.lightningkite.khrysalis.views.loadImage>android.widget.ImageView
let canvasElement = document.createElement("canvas");
export function androidWidgetImageViewLoadImage(this_: HTMLImageElement, image: (Image | null)) {
    post(() => {
        if (image instanceof ImageRaw) {
            const url = URL.createObjectURL(new Blob([image.raw]));
            this_.src = url;
        } else if (image instanceof ImageReference) {
            const reader = new FileReader();
            reader.onload = (e) => {
                const reader = e.target;
                if (reader !== null) {
                    this_.src = reader.result as string;
                }
            }
            reader.readAsDataURL(image.uri)
        } else if (image instanceof ImageImageBitmap) {
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
            })
        } else if (image instanceof ImageRemoteUrl) {
            this_.src = image.url;
        } else if (image instanceof ImageResource) {
            imageViewSetImageResource(this_, image.resource)
        }
    })
}

export function imageViewSetImageResource(this_: HTMLImageElement, resource: DrawableResource){
    let path = resource.filePath;
    if (path) {
        this_.src = path;
    } else {
        //Not perfect, because it replaces the background.
        setViewBackgroundClass(this_, resource.cssClass);
    }
}

// Generated by Khrysalis TypeScript converter
// File: views/View.ext.actual.kt
// Package: com.lightningkite.khrysalis.views

//! Declares com.lightningkite.khrysalis.views.onClick>android.view.View
export function androidViewViewOnClick(this_: HTMLElement, disabledMilliseconds: number = 500, action: () => void): void {
    let lastActivated = Date.now();

    this_.addEventListener("onclick", (_ev) => {
        _ev.stopPropagation();
        const it = _ev.target as HTMLElement;
        if (Date.now() - lastActivated > disabledMilliseconds) {
            action();
            lastActivated = Date.now();
        }
    });
}

//! Declares com.lightningkite.khrysalis.views.onLongClick>android.view.View
export function androidViewViewOnLongClick(this_: HTMLElement, action: () => void): void {
    this_.addEventListener("oncontextmenu", (_ev) => {
        _ev.stopPropagation();
        const it = _ev.target as HTMLElement;
        action();
    });
}

export function getViewVisibility(this_: HTMLElement): string {
    if(this_.hidden) return "gone";
    if(this_.style.visibility === "hidden") return "invisible";
    return "visible";
}
export function setViewVisibility(this_: HTMLElement, value: string) {
    switch(value) {
        case "gone":
            this_.hidden = true;
            this_.style.visibility = "visible";
            break;
        case "visible":
            this_.hidden = false;
            this_.style.visibility = "visible";
            break;
        case "invisible":
            this_.hidden = false;
            this_.style.visibility = "hidden";
            break;
    }
}